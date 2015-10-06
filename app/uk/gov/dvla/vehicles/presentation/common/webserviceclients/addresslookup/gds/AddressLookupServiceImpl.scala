package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds

import javax.inject.Inject
import play.api.i18n.Lang
import play.api.libs.ws.WSResponse
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.{AddressResponseDto, AddressDto}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.{AddressLookupService, AddressLookupWebService}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.JsonFormats.addressFormat
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats

object AddressLookupServiceImpl {
  final val ServiceName = "gds-address-lookup-microservice"
}

final class AddressLookupServiceImpl @Inject()(ws: AddressLookupWebService, healthStats: HealthStats)
  extends AddressLookupService with DVLALogger {

  private def extractFromJson(resp: WSResponse): Seq[Address] =
    try resp.json.as[Seq[Address]]
    catch {
      case e: Throwable => Seq.empty //  return empty seq given invalid json
    }

  override def fetchAddressesForPostcode(postcode: String, trackingId: TrackingId)
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
      ws.callPostcodeWebService(postcode, trackingId).map {
        resp =>
          logMessage(trackingId, Debug, s"Http response code from GDS postcode lookup service was: ${resp.status}")
          if (resp.status == play.api.http.Status.OK) toDropDown(resp)
          else {
            logMessage(trackingId, Error,s"Post code service returned abnormally '${resp.status}: ${resp.body}'")
            Seq.empty // The service returned http code other than 200 OK
          }
      }.recover {
        case e: Throwable =>
          logMessage(trackingId, Error, s"GDS postcode lookup service error: $e")
          Seq.empty
      }
    }
  }

  def addresses(postcode: String, trackingId: TrackingId)
               (implicit lang: Lang): Future[Seq[AddressDto]] = {
    healthStats.report(AddressLookupServiceImpl.ServiceName) {
      ws.callAddresses(postcode, trackingId).map { resp =>
            val msg = s"Http response code from GDS addresses lookup service was: ${resp.status}"
            logMessage(trackingId, Debug, msg)
          if (resp.status == play.api.http.Status.OK)
            try resp.json.as[Seq[AddressDto]]
            catch {
              case e: Throwable =>
                logMessage(trackingId, Error, s"GDS postcode lookup service error: $e")
                Seq.empty //  return empty seq given invalid json
            }
          else {
            val msg = s"Post code service returned abnormally '${resp.status}: ${resp.body}'"
            logMessage(trackingId, Error, msg)
            Seq.empty // The service returned http code other than 200 OK
          }
      }.recover {
        case e: Throwable =>
          logMessage(trackingId, Error, s"GDS postcode lookup service error: $e")
          Seq.empty
      }
    }
  }

  def toDropDownFormat(addresses: Seq[AddressResponseDto]): Seq[(String, String)] =
    addresses.map(address => (address.address, address.address))

}
