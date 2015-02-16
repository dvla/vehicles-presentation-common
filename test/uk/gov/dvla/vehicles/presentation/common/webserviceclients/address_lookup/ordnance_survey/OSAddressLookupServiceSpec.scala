package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.ordnance_survey

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.http.Status.{OK, NOT_FOUND}
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import common.clientsidesession.ClearTextClientSideSessionFactory
import common.webserviceclients.addresslookup.AddressLookupService
import common.webserviceclients.addresslookup.ordnanceservey.UprnToAddressResponseDto
import common.webserviceclients.addresslookup.ordnanceservey.PostcodeToAddressResponseDto
import common.webserviceclients.addresslookup.ordnanceservey.AddressLookupServiceImpl
import common.webserviceclients.fakes.FakeAddressLookupService.PostcodeValid
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl.postcodeToAddressResponseValid
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForUprnToAddress
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl.traderUprnValid
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl.uprnToAddressResponseValid
import common.webserviceclients.fakes.FakeResponse


final class OSAddressLookupServiceSpec extends UnitSpec {

  "fetchAddressesForPostcode" should {
    "return seq when response status is 200 OK and returns results" in {
      val (service, healthStatsMock) = addressServiceMock(responseValidForPostcodeToAddress)

      val result = service.fetchAddressesForPostcode(
        PostcodeValid,
        ClearTextClientSideSessionFactory.DefaultTrackingId
      )

      whenReady(result, timeout) {
        r =>
          r.length should equal(postcodeToAddressResponseValid.addresses.length)
          r should equal(postcodeToAddressResponseValid.addresses.map(i => (i.uprn, i.address)))
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return empty seq when response status is Ok but results is empty" in {
      val (service, healthStatsMock) =
        addressServiceMock(responsePostcode(OK, PostcodeToAddressResponseDto(addresses = Seq.empty)))

      val result = service.fetchAddressesForPostcode(
        PostcodeValid,
        ClearTextClientSideSessionFactory.DefaultTrackingId
      )

      whenReady(result, timeout) {
        _ shouldBe empty
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return empty seq when response status is not 200 OK" in {
      val (service, healthStatsMock)  = addressServiceMock(responsePostcode(NOT_FOUND))

      val result = service.fetchAddressesForPostcode(
        PostcodeValid,
        ClearTextClientSideSessionFactory.DefaultTrackingId
      )

      whenReady(result, timeout) {
        _ shouldBe empty
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return empty seq when response throws" in {
      val (service, healthStatsMock)  = addressServiceMock(responseThrows)

      val result = service.fetchAddressesForPostcode(
        PostcodeValid,
        ClearTextClientSideSessionFactory.DefaultTrackingId
      )

      whenReady(result, timeout) {
        _ shouldBe empty
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return empty seq given invalid json" in {
      val inputAsJson = Json.obj("addresses" -> "INVALID")
      val (service, healthStatsMock)  = addressServiceMock(response(OK, inputAsJson))

      val result = service.fetchAddressesForPostcode(
        PostcodeValid,
        ClearTextClientSideSessionFactory.DefaultTrackingId
      )

      whenReady(result, timeout) {
        _ shouldBe empty
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }
  }

  "fetchAddressForUprn" should {
    "return AddressViewModel when response status is 200 OK" in {
      val (service, healthStatsMock)  = addressServiceMock(responseValidForUprnToAddress)

      val result = service.fetchAddressForUprn(
        traderUprnValid.toString,
        ClearTextClientSideSessionFactory.DefaultTrackingId
      )

      whenReady(result, timeout) {
        case Some(addressViewModel) =>
          addressViewModel.uprn.map(_.toString) should equal(Some(traderUprnValid.toString))
          addressViewModel.address === uprnToAddressResponseValid.addressViewModel.get.address
        case _ => fail("Should have returned Some(AddressViewModel)")
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return None when response status not 200 OK" in {
      val (service, healthStatsMock) =
        addressServiceMock(responseUprn(NOT_FOUND, UprnToAddressResponseDto(addressViewModel = None)))

      val result = service.fetchAddressForUprn(
        traderUprnValid.toString,
        ClearTextClientSideSessionFactory.DefaultTrackingId
      )

      whenReady(result, timeout) {
        _ should equal(None)
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return none when response status is 200 OK but results is empty" in {
      val (service, healthStatsMock)  = addressServiceMock(responseUprn(OK, UprnToAddressResponseDto(addressViewModel = None)))

      val result = service.fetchAddressForUprn(traderUprnValid.toString, ClearTextClientSideSessionFactory.DefaultTrackingId)

      whenReady(result, timeout) {
        _ should equal(None)
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return none when web service throws an exception" in {
      val (service, healthStatsMock)  = addressServiceMock(responseThrows)

      val result = service.fetchAddressForUprn(traderUprnValid.toString, ClearTextClientSideSessionFactory.DefaultTrackingId)

      whenReady(result) {
        _ should equal(None)
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }

    "return empty seq given invalid json" in {
      val inputAsJson = Json.obj("addressViewModel" -> "INVALID")
      val (service, healthStatsMock)  = addressServiceMock(response(OK, inputAsJson))

      val result = service.fetchAddressForUprn(PostcodeValid, ClearTextClientSideSessionFactory.DefaultTrackingId)

      whenReady(result, timeout) {
        _ shouldBe empty
      }
      verify(healthStatsMock).report("os-address-lookup-microservice")(result)
    }
  }

  private def addressServiceMock(response: Future[WSResponse]): (AddressLookupService, HealthStats) = {
    val healthStatsMock = mock[HealthStats]
    when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
      override def answer(invocation: InvocationOnMock): Future[_] = invocation.getArguments()(1).asInstanceOf[Future[_]]
    })

    // Using the real address lookup service but passing in a fake web service that returns the responses we specify.
    (new AddressLookupServiceImpl(
      new FakeAddressLookupWebServiceImpl(responseOfPostcodeWebService = response, responseOfUprnWebService = response),
      healthStatsMock
    ), healthStatsMock)
  }

  private def response(statusCode: Int, inputAsJson: JsValue): Future[WSResponse] = Future {
    FakeResponse(status = statusCode, fakeJson = Some(inputAsJson))
  }

  private def responsePostcode(statusCode: Int,
                       input: PostcodeToAddressResponseDto = postcodeToAddressResponseValid): Future[WSResponse] = {
    val inputAsJson = Json.toJson(input)
    response(statusCode, inputAsJson)
  }

  private def responseUprn(statusCode: Int,
                   input: UprnToAddressResponseDto = uprnToAddressResponseValid): Future[WSResponse] = {
    val inputAsJson = Json.toJson(input)
    response(statusCode, inputAsJson)
  }

  private val responseThrows: Future[WSResponse] = Future {
    throw new RuntimeException("This error is generated deliberately by a test")
  }
}
