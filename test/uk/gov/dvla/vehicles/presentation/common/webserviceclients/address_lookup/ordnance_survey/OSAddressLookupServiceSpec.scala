package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.ordnance_survey

import org.joda.time.Instant
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.UnitSpec
import common.clientsidesession.ClearTextClientSideSessionFactory
import common.services.DateService
import common.webserviceclients.addresslookup.AddressLookupService
import common.webserviceclients.addresslookup.ordnanceservey.AddressLookupServiceImpl
import common.webserviceclients.addresslookup.ordnanceservey.PostcodeToAddressResponseDto
import common.webserviceclients.fakes.FakeAddressLookupService.PostcodeValid
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl.postcodeToAddressResponseValid
import common.webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress
import common.webserviceclients.fakes.FakeResponse
import common.webserviceclients.healthstats.{HealthStats, HealthStatsFailure, HealthStatsSuccess}

class OSAddressLookupServiceSpec extends UnitSpec {
  val dateService = mock[DateService]
  when(dateService.now).thenReturn(new Instant(0))

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
          r should equal(postcodeToAddressResponseValid.addresses.map(i => (i.address, i.address)))
      }
      verify(healthStatsMock).success(
        new HealthStatsSuccess("os-address-lookup-microservice", dateService.now)
      )
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
      verify(healthStatsMock).success(
        new HealthStatsSuccess("os-address-lookup-microservice", dateService.now)
      )
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
      verify(healthStatsMock).failure(
        new HealthStatsFailure("os-address-lookup-microservice", dateService.now, any[Exception])
      )
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
      verify(healthStatsMock).failure(
        new HealthStatsFailure("os-address-lookup-microservice", dateService.now, responseThrowsException)
      )
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
      verify(healthStatsMock).success(
        new HealthStatsSuccess("os-address-lookup-microservice", dateService.now)
      )
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
      dateService, healthStatsMock
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

  val responseThrowsException = new RuntimeException("This error is generated deliberately by a test")

  private val responseThrows: Future[WSResponse] = Future {
    throw responseThrowsException
  }
}
