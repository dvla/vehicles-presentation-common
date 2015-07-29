package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds

import javax.inject.Inject
import play.api.Logger
import play.api.i18n.Lang
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressDto
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.{AddressLookupService, AddressLookupWebService}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.JsonFormats.addressFormat
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object AddressLookupServiceImpl {
  final val ServiceName = "gds-address-lookup-microservice"
}

final class AddressLookupServiceImpl @Inject()(ws: AddressLookupWebService, healthStats: HealthStats)
  extends AddressLookupService {

  private def extractFromJson(resp: WSResponse): Seq[Address] =
    try resp.json.as[Seq[Address]]
    catch {
      case e: Throwable => Seq.empty //  return empty seq given invalid json
    }

  override def fetchAddressesForPostcode(postcode: String, trackingId: TrackingId, showBusinessName: Option[Boolean] = None)
                                        (implicit lang: Lang): Future[Seq[(String, String)]] = {
    def sort(addresses: Seq[Address]): Seq[Address] = {
      addresses.sortBy(addressDpa => {
        val buildingNumber = addressDpa.houseNumber.getOrElse("0")
        val buildingNumberSanitised = buildingNumber.replaceAll("[^0-9]", "")
        // Sanitise building number as it could contain letters which would cause toInt to throw e.g. 107a.
        (buildingNumberSanitised, addressDpa.houseName) // TODO check with BAs how they would want to sort the list
      })
    }

    def toDropDown(resp: WSResponse): Seq[(String, String)] = {
      val addresses = extractFromJson(resp)
      sort(addresses) map { address => (address.presentation.uprn, address.toViewModel.mkString(", ")) }
      // Sort before translating to drop down format.
    }

    healthStats.report(AddressLookupServiceImpl.ServiceName) {
      ws.callPostcodeWebService(postcode, trackingId, showBusinessName).map {
        resp =>
          Logger.debug(s"Http response code from GDS postcode lookup service " +
            s"was: ${resp.status} - trackingId: $trackingId")
          if (resp.status == play.api.http.Status.OK) toDropDown(resp)
          else {
            Logger.error(s"Post code service returned abnormally " +
              s"'${resp.status}: ${resp.body}' - trackingId: $trackingId")
            Seq.empty // The service returned http code other than 200 OK
          }
      }.recover {
        case e: Throwable =>
          Logger.error(s"GDS postcode lookup service error: $e")
          Seq.empty
      }
    }
  }

  override def fetchAddressForUprn(uprn: String, trackingId: TrackingId)
                                  (implicit lang: Lang): Future[Option[AddressModel]] = {
    def toViewModel(resp: WSResponse) = {
      val addresses = extractFromJson(resp)
      require(addresses.length >= 1, s"Should be at least one address for the UPRN: $uprn")
      Some(AddressModel(uprn = Some(addresses.head.presentation.uprn.toLong), address = addresses.head.toViewModel))
      // Translate to view model.
    }

    healthStats.report(AddressLookupServiceImpl.ServiceName) {
      ws.callUprnWebService(uprn, trackingId).map { resp =>
        Logger.debug(s"Http response code from GDS postcode lookup service was: ${resp.status}")
        if (resp.status == play.api.http.Status.OK) toViewModel(resp)
        else {
          Logger.error(s"UPRN service returned abnormally " +
            s"'${resp.status}: ${resp.body}' - trackingId: $trackingId")
          None
        }
      }.recover {
        case e: Throwable =>
          Logger.error(s"GDS uprn lookup service error: $e - trackingId: $trackingId")
          None
      }
    }
  }

  def addresses(postcode: String, trackingId: TrackingId)
               (implicit lang: Lang): Future[Seq[AddressDto]] = {
    healthStats.report(AddressLookupServiceImpl.ServiceName) {
      ws.callAddresses(postcode, trackingId).map { resp =>
          Logger.debug(s"Http response code from GDS addresses lookup service " +
            s"was: ${resp.status} - trackingId: $trackingId")
          if (resp.status == play.api.http.Status.OK)
            try resp.json.as[Seq[AddressDto]]
            catch {
              case e: Throwable =>
                Logger.error(s"GDS postcode lookup service error: $e - trackingId: $trackingId")
                Seq.empty //  return empty seq given invalid json
            }
          else {
            Logger.error(s"Post code service returned abnormally " +
              s"'${resp.status}: ${resp.body}' - trackingId: $trackingId")
            Seq.empty // The service returned http code other than 200 OK
          }
      }.recover {
        case e: Throwable =>
          Logger.error(s"GDS postcode lookup service error: $e - trackingId: $trackingId")
          Seq.empty
      }
    }
  }
}
