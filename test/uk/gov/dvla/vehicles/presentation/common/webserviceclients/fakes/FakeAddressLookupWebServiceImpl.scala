package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import play.api.http.Status.OK
import play.api.i18n.Lang
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupWebService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.{AddressResponseDto, UprnToAddressResponseDto, PostcodeToAddressResponseDto}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.Details
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.Location
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.Presentation
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeAddressLookupService.PostcodeWithoutAddresses
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeAddressLookupService.PostcodeValid

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
  final val traderUprnValid = 12345L // GDS address lookup service specific
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

  def gdsAddress(presentationProperty: String = "property stub", presentationStreet: String = "123"): Address =
    Address(
      gssCode = "gssCode stub",
      countryCode = "countryCode stub",
      postcode = PostcodeValid,
      houseName = Some("presentationProperty stub"),
      houseNumber = Some("123"),
      presentation = Presentation(property = Some(presentationProperty),
        street = Some(presentationStreet),
        town = Some("town stub"),
        area = Some("area stub"),
        postcode = PostcodeValid,
        uprn = traderUprnValid.toString),
      details = Details(
        usrn = "usrn stub",
        isResidential = true,
        isCommercial = true,
        isPostalAddress = true,
        classification = "classification stub",
        state = "state stub",
        organisation = Some("organisation stub")
      ),
      location = Location(
        x = 1.0d,
        y = 2.0d)
    )

  def responseValidForGdsAddressLookup: Future[WSResponse] = {
    import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds.domain.JsonFormats._
    val inputAsJson = Json.toJson(Seq(gdsAddress(), gdsAddress(presentationStreet = "456")))

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }
}
