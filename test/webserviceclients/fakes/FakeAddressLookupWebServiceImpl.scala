package webserviceclients.fakes

import play.api.http.Status.OK
import play.api.i18n.Lang
import play.api.libs.json.Json
import play.api.libs.ws.Response
import webserviceclients.address_lookup.ordnance_survey.{UprnToAddressResponseDto, UprnAddressPairDto, PostcodeToAddressResponseDto}
import viewmodels.AddressViewModel
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import webserviceclients.address_lookup.AddressLookupWebService
import webserviceclients.address_lookup.gds.domain.Address
import webserviceclients.address_lookup.gds.domain.Details
import webserviceclients.address_lookup.gds.domain.Location
import webserviceclients.address_lookup.gds.domain.Presentation
import webserviceclients.fakes.FakeAddressLookupService.PostcodeWithoutAddresses
import webserviceclients.fakes.FakeAddressLookupService.PostcodeValid

final class FakeAddressLookupWebServiceImpl(responseOfPostcodeWebService: Future[Response],
                                            responseOfUprnWebService: Future[Response]) extends AddressLookupWebService {
  override def callPostcodeWebService(postcode: String, trackingId: String)
                                     (implicit lang: Lang): Future[Response] =
    if (postcode == PostcodeWithoutAddresses.toUpperCase) Future {
      FakeResponse(status = OK, fakeJson = None)
    }
    else responseOfPostcodeWebService

  override def callUprnWebService(uprn: String, trackingId: String)
                                 (implicit lang: Lang): Future[Response] = responseOfUprnWebService
}

object FakeAddressLookupWebServiceImpl {
  final val traderUprnValid = 12345L
  final val traderUprnValid2 = 4567L

  private def addressSeq(houseName: String, houseNumber: String): Seq[String] = {
    Seq(houseName, houseNumber, "property stub", "street stub", "town stub", "area stub", PostcodeValid)
  }

  def uprnAddressPairWithDefaults(uprn: String = traderUprnValid.toString, houseName: String = "presentationProperty stub", houseNumber: String = "123") =
    UprnAddressPairDto(uprn, address = addressSeq(houseName, houseNumber).mkString(", "))

  def postcodeToAddressResponseValid: PostcodeToAddressResponseDto = {
    val results = Seq(
      uprnAddressPairWithDefaults(),
      uprnAddressPairWithDefaults(uprn = "67890", houseNumber = "456"),
      uprnAddressPairWithDefaults(uprn = "111213", houseNumber = "789")
    )

    PostcodeToAddressResponseDto(addresses = results)
  }

  def responseValidForPostcodeToAddress: Future[Response] = {
    val inputAsJson = Json.toJson(postcodeToAddressResponseValid)

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }

  def responseValidForPostcodeToAddressNotFound: Future[Response] = {
    val inputAsJson = Json.toJson(PostcodeToAddressResponseDto(addresses = Seq.empty))

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }

  val uprnToAddressResponseValid = {
    val uprnAddressPair = uprnAddressPairWithDefaults()
    UprnToAddressResponseDto(addressViewModel = Some(AddressViewModel(uprn = Some(uprnAddressPair.uprn.toLong), address = uprnAddressPair.address.split(", "))))
  }

  def responseValidForUprnToAddress: Future[Response] = {
    val inputAsJson = Json.toJson(uprnToAddressResponseValid)

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }

  def responseValidForUprnToAddressNotFound: Future[Response] = {
    val inputAsJson = Json.toJson(UprnToAddressResponseDto(addressViewModel = None))

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

  def responseValidForGdsAddressLookup: Future[Response] = {
    import webserviceclients.address_lookup.gds.domain.JsonFormats._
    val inputAsJson = Json.toJson(Seq(gdsAddress(), gdsAddress(presentationStreet = "456")))

    Future {
      FakeResponse(status = OK, fakeJson = Some(inputAsJson))
    }
  }
}
