package filters

import java.util.Date

import AccessLoggingFilterSpec.testDate
import com.google.inject.Guice
import com.google.inject.name.Names
import com.tzavellas.sse.guice.ScalaModule
import filters.AccessLoggingFilter.AccessLoggerName
import helpers.UnitSpec
import org.mockito.Mockito
import play.api.LoggerLike
import play.api.http.HeaderNames.CONTENT_LENGTH
import play.api.mvc.{Cookie, SimpleResult, RequestHeader, Results, AnyContentAsEmpty}
import play.api.test.{FakeHeaders, FakeRequest}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import webserviceclients.HttpHeaders
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.existentials

class AccessLoggingFilterSpec extends UnitSpec {

  "Log an incoming request" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      val trackingIdCookie = Cookie(ClientSideSessionFactory.TrackingIdCookieName, "98765")
      val filterResult: Future[SimpleResult] = filter.apply(nextFilter)(request.withCookies(trackingIdCookie))

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        info("The log entry should look like 127.0.0.1 - - [dd/MMM/yyyy:hh:mm:ss +SSS] \"GET / HTTP/1.1\" 200 12345 \"98765\"")
        loggerInfo should startWith("127.0.0.1")
        loggerInfo should include("GET /")
        loggerInfo should include("HTTP/1.1")
        loggerInfo should include("200")
        loggerInfo should include("12345")
        loggerInfo should include("98765")
        loggerInfo should include(s"[${ClfEntryBuilder.dateFormat.format(testDate)}]")
      }
  }

  "Log an incoming request with ipAddress provided in XForwardedFor only" in setUp("127.0.0.3") {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[SimpleResult] = filter.apply(nextFilter)(
        request
          .withHeaders(HttpHeaders.XForwardedFor -> "127.0.0.2")
          .withHeaders(HttpHeaders.XRealIp -> "127.0.0.4")
      )

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should startWith("127.0.0.2")
      }
  }

  "Log an incoming request with ipAddress provided in remoteAddress only" in setUp("127.0.0.3") {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[SimpleResult] =
        filter.apply(nextFilter)(request.withHeaders(HttpHeaders.XRealIp -> "127.0.0.4"))

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should startWith("127.0.0.3")
      }
  }

  "Log an incoming request with ipAddress provided in XRealIp only" in setUp(null) {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[SimpleResult] =
        filter.apply(nextFilter)(request.withHeaders(HttpHeaders.XRealIp -> "127.0.0.4"))

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should startWith("127.0.0.4")
      }
  }

  "Log an incoming request with no ipAddress provided" in setUp(null) {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[SimpleResult] = filter.apply(nextFilter)(request)

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should startWith("-")
      }
  }

  "not log request to /healthcheck" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      whenReady(filter.apply(nextFilter)(FakeRequest("GET", "http://localhost/healthcheck"))) { result =>
        Mockito.verifyNoMoreInteractions(logger.logger)
      }
  }

  "not log request to /healthcheck with request parameters" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      whenReady(filter.apply(nextFilter)(FakeRequest("GET", "http://localhost/healthcheck?a=b&c=d"))) { result =>
        Mockito.verifyNoMoreInteractions(logger.logger)
      }
  }

  "log request with /healthcheck/some/axtra/path" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val fakeRequest = FakeRequest("GET", "http://localhost/healthcheck/some/extra/path")
      whenReady(filter.apply(nextFilter)(fakeRequest)) { result =>
        logger.captureLogInfo()
      }
  }

  private class MockFilter extends ((RequestHeader) => Future[SimpleResult]) {
    var passedRequest: RequestHeader = _

    override def apply(rh: RequestHeader): Future[SimpleResult] = {
      passedRequest = rh
      Future(Results.Ok)
    }
  }

  private case class SetUp(filter: AccessLoggingFilter,
                           request: FakeRequest[_],
                           sessionFactory: ClientSideSessionFactory,
                           nextFilter: MockFilter,
                           logger: MockLogger)

  private def setUp(ipAddress: String = "127.0.0.1")(test: SetUp => Any) {
    val sessionFactory = mock[ClientSideSessionFactory]
    val accessLogger = new MockLogger

    class TestClfEntryBuilder extends ClfEntryBuilder {
      import AccessLoggingFilterSpec.testDate

      override def clfEntry(requestTimestamp: Date, request: RequestHeader, result: SimpleResult): String = {
        val extendedResult = result.withHeaders(CONTENT_LENGTH -> "12345").
          withHeaders(ClientSideSessionFactory.TrackingIdCookieName -> "98765")
        super.clfEntry(testDate, request, extendedResult)
      }
    }

    val injector = Guice.createInjector(testModule(new ScalaModule {
      override def configure(): Unit = {
        bind[ClfEntryBuilder].toInstance(new TestClfEntryBuilder())
        bind[LoggerLike].annotatedWith(Names.named(AccessLoggerName)).toInstance(accessLogger)
      }
    }))

    test(SetUp(
      filter = injector.getInstance(classOf[AccessLoggingFilter]),
      request = FakeRequest("GET", "/", FakeHeaders(), AnyContentAsEmpty, remoteAddress = ipAddress),
      sessionFactory = sessionFactory,
      nextFilter = new MockFilter(),
      logger = accessLogger
    ))
  }
}

private object AccessLoggingFilterSpec {
  val testDate = new Date()
}