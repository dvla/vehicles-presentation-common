package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import play.api.http.Status.OK
import play.api.i18n.Lang
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupWebService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressDto
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeAddressLookupService.PostcodeWithoutAddresses

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class FakeAddressLookupWebServiceImpl(responseOfPostcodeWebService: Future[WSResponse],
                                            responseOfUprnWebService: Future[WSResponse]) extends AddressLookupWebService {
  override def callAddresses(postcode: String, trackingId: TrackingId)(implicit lang: Lang): Future[WSResponse] =
      if (postcode == PostcodeWithoutAddresses.toUpperCase) Future {
        FakeResponse(status = OK, fakeJson = None)
      }
      else responseOfPostcodeWebService
}

object FakeAddressLookupWebServiceImpl {
  final val selectedAddress = "presentationProperty stub, 123, property stub, street stub, town stub, area stub, QQ99QQ"

  def postcodeToAddressResponseValid: Seq[AddressDto] = {
    val results = Seq(
      AddressDto(s"home 1, Sometown, SA11AA",
        None,
        s"1",
        None,
        None,
        s"Sometown",
        s"SA11AA")
    )
    results
  }

  def responseValidForPostcodeToAddress: Future[WSResponse] = {
    val inputAsJson = Json.toJson(postcodeToAddressResponseValid)

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }

}
