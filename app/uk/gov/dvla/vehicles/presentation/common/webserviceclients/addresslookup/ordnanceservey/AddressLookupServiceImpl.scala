package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import javax.inject.Inject

import play.api.i18n.Lang
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.{AddressLookupService, AddressLookupWebService}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.{HealthStats, HealthStatsFailure, HealthStatsSuccess}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AddressLookupServiceImpl {
  final val ServiceName = "os-address-lookup-microservice"
}

class AddressLookupServiceImpl @Inject()(ws: AddressLookupWebService,
                                               dateService: DateService,
                                               healthStats: HealthStats) extends AddressLookupService with DVLALogger {
  import AddressLookupServiceImpl.ServiceName

  def addressesToDropDown(postcode: String, trackingId: TrackingId)
                         (implicit lang: Lang): Future[Seq[(String, String)]] = {
    val addressesFound = addresses(postcode, trackingId)
    addressesFound.map { addresses =>
      toDropDownFormat(addresses).sortWith(_._2 < _._2)
    }
  }

  def addresses(postcode: String, trackingId: TrackingId)
               (implicit lang: Lang): Future[Seq[AddressDto]] = {
    ws.callAddresses(postcode, trackingId)(lang).map { resp =>
      val msg = s"Http response code from Ordnance Survey postcode lookup service was: ${resp.status}"
      logMessage(trackingId, Debug, msg)
      if (resp.status == play.api.http.Status.OK) {
        healthStats.success(HealthStatsSuccess(ServiceName, dateService.now))
        try resp.json.as[Seq[AddressDto]]
        catch {
          case e: Throwable =>
            logMessage(trackingId, Error, s"Ordnance Survey postcode lookup service error: $e")
            Seq.empty //  return empty seq given invalid json
        }
      }
      else {
        val msg = s"Post code service returned abnormally '${resp.status}: ${resp.body}'"
        logMessage(trackingId, Error, msg)
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, new Exception()))
        Seq.empty // The service returned http code other than 200 OK
      }
    }.recover {
      case e: Throwable =>
        logMessage(trackingId, Error, s"Ordnance Survey postcode lookup service error: $e")
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, e))
        Seq.empty // Exception case and empty seq case are treated the same in the UI
    }
  }

    private def toDropDownFormat(addresses: Seq[AddressDto]): Seq[(String, String)] =
        addresses.map(aR => ( (aR.addressLine, aR.businessName) match {
          case (_, Some(businessName)) =>
            // do not include business name in value part because we don't want it part of the address playback (in VM at least)
            if (!aR.addressLine.contains(businessName)) (aR.addressLine, businessName + ", " + aR.addressLine)
            else (aR.addressLine, aR.addressLine)
          case (_, _) => (aR.addressLine, aR.addressLine)
      }))

}
