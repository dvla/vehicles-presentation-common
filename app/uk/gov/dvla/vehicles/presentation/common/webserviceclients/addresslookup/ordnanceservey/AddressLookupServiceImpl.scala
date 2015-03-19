package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import javax.inject.Inject
import play.api.Logger
import play.api.i18n.Lang
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.{AddressLookupService, AddressLookupWebService}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.{HealthStatsFailure, HealthStatsSuccess, HealthStats}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object AddressLookupServiceImpl {
  final val ServiceName = "os-address-lookup-microservice"
}

final class AddressLookupServiceImpl @Inject()(ws: AddressLookupWebService,
                                               dateService: DateService,
                                               healthStats: HealthStats) extends AddressLookupService {
  import AddressLookupServiceImpl.ServiceName

  override def fetchAddressesForPostcode(postcode: String, trackingId: String, showBusinessName: Option[Boolean] = None)
                                        (implicit lang: Lang): Future[Seq[(String, String)]] = {

    def extractFromJson(resp: WSResponse): Option[PostcodeToAddressResponseDto] =
      resp.json.asOpt[PostcodeToAddressResponseDto]

    def toDropDown(resp: WSResponse): Seq[(String, String)] =
      extractFromJson(resp) match {
        case Some(results) =>
          results.addresses.map(address => (address.uprn, address.address))
        case None =>
          // Handle no results
          val postcodeToLog = LogFormats.anonymize(postcode)
          Logger.debug(s"No results returned for postcode: $postcodeToLog - trackingId: $trackingId")
          Seq.empty// Exception case and empty seq case are treated the same in the UI
      }

    ws.callPostcodeWebService(postcode, trackingId, showBusinessName)(lang).map { resp =>
      Logger.debug(s"Http response code from Ordnance Survey postcode lookup " +
        s"service was: ${resp.status} - trackingId: $trackingId")
      if (resp.status == play.api.http.Status.OK) {
        healthStats.success(HealthStatsSuccess(ServiceName, dateService.now))
        toDropDown(resp)
      }
      else {
        Logger.error(s"Post code service returned abnormally " +
          s"'${resp.status}: ${resp.body}' - trackingId: $trackingId")
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, new Exception()))
        Seq.empty // The service returned http code other than 200 OK
      }
    }.recover {
      case e: Throwable =>
        Logger.error(s"Ordnance Survey postcode lookup service error. - trackingId: $trackingId", e)
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, e))
        Seq.empty // Exception case and empty seq case are treated the same in the UI
    }
  }

  override def fetchAddressForUprn(uprn: String, trackingId: String)
                                  (implicit lang: Lang): Future[Option[AddressModel]] = {

    // Extract result from response and return as a view model.
    def extractFromJson(resp: WSResponse): Option[UprnToAddressResponseDto] = {
      resp.json.asOpt[UprnToAddressResponseDto]
    }

    def toViewModel(resp: WSResponse) =
      extractFromJson(resp) match {
        case Some(deserialized) => deserialized.addressViewModel
        case None =>
          val uprnToLog = LogFormats.anonymize(uprn)
          Logger.error(s"Could not deserialize response of web service for " +
            s"submitted UPRN: $uprnToLog - trackingId: $trackingId")
          None
      }

    ws.callUprnWebService(uprn, trackingId).map { resp =>
      Logger.debug(s"Http response code from Ordnance Survey uprn lookup " +
        s"service was: ${resp.status} - trackingId: $trackingId")
      if (resp.status == play.api.http.Status.OK) {
        healthStats.success(HealthStatsSuccess(ServiceName, dateService.now))
        toViewModel(resp)
      } else {
        Logger.error(s"Post code service returned abnormally " +
          s"'${resp.status}: ${resp.body}' - trackingId: $trackingId")
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, new Exception()))
        None
      }
    }.recover {
      case e: Throwable =>
        Logger.error(s"Ordnance Survey postcode lookup service error - trackingId: $trackingId", e)
        healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, e))
        None
    }
  }
}
