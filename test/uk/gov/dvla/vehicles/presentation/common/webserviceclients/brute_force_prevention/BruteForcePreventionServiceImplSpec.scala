package uk.gov.dvla.vehicles.presentation.common.webserviceclients.brute_force_prevention

import org.mockito.Mockito.when
import org.mockito.Matchers.anyString
import play.api.http.Status.{FORBIDDEN, OK}
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import uk.gov.dvla.vehicles.presentation.common
import common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention._
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.responseFirstAttempt
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.responseSecondAttempt
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.VrmThrows
import common.webserviceclients.fakes.FakeVehicleLookupWebService.RegistrationNumberValid
import common.webserviceclients.fakes.{FakeDateServiceImpl, FakeResponse}

final class BruteForcePreventionServiceImplSpec extends UnitSpec {
  "isVrmLookupPermitted" should {
    "return true when response status is 200 OK" in {
      val service = bruteForceServiceImpl(permitted = true)
      whenReady(service.isVrmLookupPermitted(RegistrationNumberValid), timeout) {
        case viewModel =>
          viewModel.permitted should equal(true)
          viewModel.attempts should equal(1)
          viewModel.maxAttempts should equal(3)
          viewModel.dateTimeISOChronology should startWith("1970-11-25T00:00:00.000")
      }
    }

    "return false when response status is not 200 OK" in {
      val service = bruteForceServiceImpl(permitted = false)
      whenReady(service.isVrmLookupPermitted(RegistrationNumberValid)) {
        case viewModel =>
          viewModel.permitted should equal(false)
          viewModel.attempts should equal(1)
          viewModel.maxAttempts should equal(3)
          viewModel.dateTimeISOChronology should startWith("1970-11-25T00:00:00.000")
      }
    }

    "fail future when webservice call throws exception" in {
      val service = bruteForceServiceImpl(permitted = true)
      val result = service.isVrmLookupPermitted(VrmThrows)

      Try(
        whenReady(result){ r => fail("we expect whenReady to throw an exception") }
      ).isFailure should equal(true)
    }
  }

  "reset" should {
    "return the http code from the underlying web service" in {
      val bruteForcePreventionWebServiceMock: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]
      when(bruteForcePreventionWebServiceMock.reset(anyString)).thenReturn(Future {
        new FakeResponse(status = OK)
      })

      val service = new BruteForcePreventionServiceImpl(
        new TestBruteForcePreventionConfig,
        ws = bruteForcePreventionWebServiceMock,
        dateService = new FakeDateServiceImpl
      )
      whenReady(service.reset("A1")) {
        case httpCode: Int =>
          httpCode should equal(OK)
      }
    }

    "fail future when webservice call throws exception" in {
      val bruteForcePreventionWebServiceMock: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]
      when(bruteForcePreventionWebServiceMock.reset(anyString)).thenReturn(responseThrows)

      val service = new BruteForcePreventionServiceImpl(
        new TestBruteForcePreventionConfig,
        ws = bruteForcePreventionWebServiceMock,
        dateService = new FakeDateServiceImpl
      )
      val result = service.reset("A1")
      Try(
        whenReady(result){ r => fail("we expect whenReady to throw an exception") }
      ).isFailure should equal(true)
    }
  }

  private def responseThrows: Future[WSResponse] = Future {
    throw new RuntimeException("This error is generated deliberately by a test")
  }

  private def bruteForceServiceImpl(permitted: Boolean): BruteForcePreventionService = {
    def bruteForcePreventionWebService: BruteForcePreventionWebService = {
      val status = if (permitted) OK else FORBIDDEN
      val bruteForcePreventionWebService: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]

      when(bruteForcePreventionWebService.callBruteForce(RegistrationNumberValid)).thenReturn(Future {
        new FakeResponse(status = status, fakeJson = responseFirstAttempt)
      })
      when(bruteForcePreventionWebService.callBruteForce(FakeBruteForcePreventionWebServiceImpl.VrmAttempt2))
        .thenReturn(Future {
          new FakeResponse(status = status, fakeJson = responseSecondAttempt)
        })
      when(bruteForcePreventionWebService.callBruteForce(FakeBruteForcePreventionWebServiceImpl.VrmLocked))
        .thenReturn(Future {
          new FakeResponse(status = status)
        })
      when(bruteForcePreventionWebService.callBruteForce(VrmThrows)).thenReturn(responseThrows)

      bruteForcePreventionWebService
    }

    new BruteForcePreventionServiceImpl(
      new TestBruteForcePreventionConfig,
      ws = bruteForcePreventionWebService,
      dateService = new FakeDateServiceImpl
    )
  }
}
