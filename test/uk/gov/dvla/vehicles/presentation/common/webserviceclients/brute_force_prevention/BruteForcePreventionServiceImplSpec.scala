package uk.gov.dvla.vehicles.presentation.common.webserviceclients.brute_force_prevention

import org.joda.time.Instant
import org.mockito.Mockito.{when, verify}
import org.mockito.Matchers.{anyString, any}
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.http.Status.{FORBIDDEN, OK}
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.{HealthStats}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import uk.gov.dvla.vehicles.presentation.common
import common.UnitSpec
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionWebService
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.responseFirstAttempt
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.responseSecondAttempt
import common.webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.VrmThrows
import common.webserviceclients.fakes.{FakeDateServiceImpl, FakeResponse}

class BruteForcePreventionServiceImplSpec extends UnitSpec {
  private final val RegistrationNumberValid = "AB12AWR"
  private val trackingId = TrackingId("testtrackingId")
  "isVrmLookupPermitted" should {
    "return true when response status is 200 OK" in {
      val (service, healthStatsMock, dateService) = bruteForceServiceImpl(permitted = true)
      whenReady(service.isVrmLookupPermitted(RegistrationNumberValid,trackingId), timeout) {
        case viewModel =>
          viewModel.permitted should equal(true)
          viewModel.attempts should equal(1)
          viewModel.maxAttempts should equal(3)
          viewModel.dateTimeISOChronology should startWith("1970-11-25T00:00:00.000") //TODO use regex pattern match
      }
    }

    "return false when response status is not 200 OK" in {
      val (service, _, _) = bruteForceServiceImpl(permitted = false)
      whenReady(service.isVrmLookupPermitted(RegistrationNumberValid,trackingId)) {
        case viewModel =>
          viewModel.permitted should equal(false)
          viewModel.attempts should equal(1)
          viewModel.maxAttempts should equal(3)
          viewModel.dateTimeISOChronology should startWith("1970-11-25T00:00:00.000") //TODO use regex pattern match
      }
    }

    "fail future when webservice call throws exception" in {
      val (service, healthStatsMock, dateService) = bruteForceServiceImpl(permitted = true)
      val result = service.isVrmLookupPermitted(VrmThrows,trackingId)

      Try(
        whenReady(result){ r => fail("we expect whenReady to throw an exception") }
      ).isFailure should equal(true)

      verify(healthStatsMock).report("bruteforce-prevention-microservice")(result)
    }
  }

  "reset" should {
    "return the http code from the underlying web service" in {
      val bruteForcePreventionWebServiceMock: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]
      when(bruteForcePreventionWebServiceMock.reset(anyString, any[TrackingId])).thenReturn(Future {
        new FakeResponse(status = OK)
      })

      val dateService = new FakeDateServiceImpl {
        override def now = new Instant(3466)
      }

      val healthStatsMock = mock[HealthStats]
      when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
        override def answer(invocation: InvocationOnMock): Future[_] = invocation.getArguments()(1).asInstanceOf[Future[_]]
      })

      val service = new BruteForcePreventionServiceImpl(
        new TestBruteForcePreventionConfig,
        ws = bruteForcePreventionWebServiceMock,
        healthStatsMock,
        dateService = dateService
      )
      val result = service.reset("A1", trackingId)
      whenReady(result) {
        case httpCode: Int =>
          httpCode should equal(OK)
          verify(healthStatsMock).report("bruteforce-prevention-microservice")(result)
      }
    }

    "fail future when webservice call throws exception" in {
      val bruteForcePreventionWebServiceMock: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]
      when(bruteForcePreventionWebServiceMock.reset(anyString, any[TrackingId])).thenReturn(responseThrows)

      val dateService = new FakeDateServiceImpl {
        override def now = new Instant(3466)
      }

      val healthStatsMock = mock[HealthStats]
      when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
        override def answer(invocation: InvocationOnMock): Future[_] = invocation.getArguments()(1).asInstanceOf[Future[_]]
      })

      val service = new BruteForcePreventionServiceImpl(
        new TestBruteForcePreventionConfig,
        ws = bruteForcePreventionWebServiceMock,
        healthStatsMock,
        dateService = dateService
      )
      val result = service.reset("A1", trackingId)

      result.eitherValue
      Try(
        whenReady(result) { r => fail("we expect whenReady to throw an exception") }
      ).isFailure should equal(true)
      verify(healthStatsMock).report("bruteforce-prevention-microservice")(result)
    }
  }

  private val responseThrowsException = new RuntimeException("This error is generated deliberately by a test")
  private def responseThrows: Future[WSResponse] = Future {
    throw responseThrowsException
  }

  private def bruteForceServiceImpl(permitted: Boolean): (BruteForcePreventionService, HealthStats, DateService) = {
    def bruteForcePreventionWebService: BruteForcePreventionWebService = {
      val status = if (permitted) OK else FORBIDDEN
      val bruteForcePreventionWebService: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]

      when(bruteForcePreventionWebService.callBruteForce(RegistrationNumberValid,trackingId)).thenReturn(Future {
        new FakeResponse(status = status, fakeJson = responseFirstAttempt)
      })
      when(bruteForcePreventionWebService.callBruteForce(FakeBruteForcePreventionWebServiceImpl.VrmAttempt2,trackingId))
        .thenReturn(Future {
          new FakeResponse(status = status, fakeJson = responseSecondAttempt)
        })
      when(bruteForcePreventionWebService.callBruteForce(FakeBruteForcePreventionWebServiceImpl.VrmLocked,trackingId))
        .thenReturn(Future {
          new FakeResponse(status = status)
        })
      when(bruteForcePreventionWebService.callBruteForce(VrmThrows,trackingId)).thenReturn(responseThrows)

      bruteForcePreventionWebService
    }

    val healthStatsMock = mock[HealthStats]
    when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
      override def answer(invocation: InvocationOnMock): Future[_] = invocation.getArguments()(1).asInstanceOf[Future[_]]
    })
    val fakeDateService =  new FakeDateServiceImpl {
      override def now = new Instant(987134)
    }

    (new BruteForcePreventionServiceImpl(
      new TestBruteForcePreventionConfig,
      ws = bruteForcePreventionWebService,
      healthStatsMock,
      dateService = fakeDateService
    ), healthStatsMock, fakeDateService)
  }
}
