package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import play.api.http.Status.OK
import play.api.i18n.Lang
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupWebService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.{AddressResponseDto, PostcodeToAddressResponseDto}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeAddressLookupService.{PostcodeValid, PostcodeWithoutAddresses}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class FakeAddressLookupWebServiceImpl(responseOfPostcodeWebService: Future[WSResponse],
                                            responseOfUprnWebService: Future[WSResponse]) extends AddressLookupWebService {
  override def callPostcodeWebService(postcode: String, trackingId: TrackingId)
                                     (implicit lang: Lang): Future[WSResponse] =
    if (postcode == PostcodeWithoutAddresses.toUpperCase) Future {
      FakeResponse(status = OK, fakeJson = None)
    }
    else responseOfPostcodeWebService

  override def callAddresses(postcode: String, trackingId: TrackingId)(implicit lang: Lang): Future[WSResponse] = ???
}

object FakeAddressLookupWebServiceImpl {
  final val selectedAddress = "presentationProperty stub, 123, property stub, street stub, town stub, area stub, QQ99QQ"

  private def addressSeq(houseName: String, houseNumber: String): Seq[String] = {
    Seq(houseName, houseNumber, "property stub", "street stub", "town stub", "area stub", PostcodeValid)
  }

  def postcodeToAddressResponseValid: PostcodeToAddressResponseDto = {
    val results = Seq(
      AddressResponseDto(addressSeq("home", "1").mkString(", "), None)
    )

    PostcodeToAddressResponseDto(addresses = results)
  }

  def responseValidForPostcodeToAddress: Future[WSResponse] = {
    val inputAsJson = Json.toJson(postcodeToAddressResponseValid)

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }

  def responseValidForPostcodeToAddressNotFound: Future[WSResponse] = {
    val inputAsJson = Json.toJson(PostcodeToAddressResponseDto(addresses = Seq.empty))

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }

}
