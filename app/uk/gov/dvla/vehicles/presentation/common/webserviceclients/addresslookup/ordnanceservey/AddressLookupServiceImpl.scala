package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import javax.inject.Inject
import play.api.i18n.Lang
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupWebService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStatsFailure
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStatsSuccess
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats

object AddressLookupServiceImpl {
  final val ServiceName = "os-address-lookup-microservice"
}

class AddressLookupServiceImpl @Inject()(ws: AddressLookupWebService,
                                               dateService: DateService,
                                               healthStats: HealthStats) extends AddressLookupService with DVLALogger {
  import AddressLookupServiceImpl.ServiceName

  override def fetchAddressesForPostcode(postcode: String,
                                         trackingId: TrackingId)
                                        (implicit lang: Lang): Future[Seq[(String, String)]] = {

    def extractFromJson(resp: WSResponse): Option[PostcodeToAddressResponseDto] =
      resp.json.asOpt[PostcodeToAddressResponseDto]

    def toDropDown(resp: WSResponse): Seq[(String, String)] =
      extractFromJson(resp) match {
        case Some(results) => toDropDownFormat(results.addresses).sortWith(_._2 < _._2)
        case None =>
          // Handle no results
          val postcodeToLog = LogFormats.anonymize(postcode)
          logMessage(trackingId, Debug, s"No results returned for postcode: $postcodeToLog")
          Seq.empty// Exception case and empty seq case are treated the same in the UI
      }

    ws.callPostcodeWebService(postcode, trackingId)(lang).map { resp =>
      val msg = s"Http response code from Ordnance Survey postcode lookup service was: ${resp.status}"
      logMessage(trackingId, Debug, msg)
      if (resp.status == play.api.http.Status.OK) {
        healthStats.success(HealthStatsSuccess(ServiceName, dateService.now))
        toDropDown(resp)
      }
      else {
        val msg = s"Post code service returned abnormally '${resp.status}: ${resp.body}'"
        logMessage(trackingId, Error, msg)
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, new Exception()))
        Seq.empty // The service returned http code other than 200 OK
      }
    }.recover {
      case e: Throwable =>
        logMessage(trackingId, Error, s"Ordnance Survey postcode lookup service error.$e")
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, e))
        Seq.empty // Exception case and empty seq case are treated the same in the UI
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

  def toDropDownFormat(addresses: Seq[AddressResponseDto]): Seq[(String, String)] =
      addresses.map(aR => ( (aR.address, aR.businessName) match {
        case (_, Some(businessName)) =>
          // do not include business name in value part because we don't want it part of the address playback (in VM at least)
          if (!aR.address.contains(businessName)) (aR.address, businessName + ", " + aR.address)
          else (aR.address, aR.address)
        case (_, _) => (aR.address, aR.address)
    }))
}
