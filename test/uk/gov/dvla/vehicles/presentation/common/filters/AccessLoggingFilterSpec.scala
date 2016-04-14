package uk.gov.dvla.vehicles.presentation.common.filters

import com.google.inject.Guice
import com.google.inject.name.Names
import com.tzavellas.sse.guice.ScalaModule
import java.util.Date
import org.mockito.Mockito
import play.api.http.HeaderNames.{CACHE_CONTROL, CONTENT_LENGTH, CONTENT_TYPE, PRAGMA}
import play.api.LoggerLike
import play.api.mvc.{AnyContentAsEmpty, Cookie, RequestHeader, Results, Result}
import play.api.test.{FakeHeaders, FakeRequest}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.existentials
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.filters.AccessLoggingFilter.AccessLoggerName
import uk.gov.dvla.vehicles.presentation.common.filters.AccessLoggingFilterSpec.testDate
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders.{XForwardedFor, XRealIp}

class AccessLoggingFilterSpec extends UnitSpec {

  val missingRemoteIpAddress = null

  "Log an incoming request" in setUp() {
    // This is the implementation of the test function (test: SetUp => Unit) done using a partial function
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      val trackingIdCookie = Cookie(ClientSideSessionFactory.TrackingIdCookieName, "98765")
      val filterResult: Future[Result] = filter.apply(nextFilter)(request.withCookies(trackingIdCookie))

      whenReady(filterResult, timeout) { result =>
        val loggerInfo = logger.captureLogInfo()
        info("The log entry should look like:[TrackingID: 98765] - 127.0.0.1 - - [dd/MMM/yyyy:hh:mm:ss +SSS] \"GET / HTTP/1.1\" 200 12345")
        loggerInfo should startWith("[TrackingID: 98765]")
        loggerInfo should include("127.0.0.1")
        loggerInfo should include("GET /")
        loggerInfo should include("HTTP/1.1")
        loggerInfo should include("200")
        loggerInfo should include("12345")
        loggerInfo should include("98765")
        loggerInfo should include(s"[${ClfEntryBuilder.dateFormat.format(testDate)}]")
      }
  }

  "Log an incoming request with ip address provided in XForwardedFor only" in setUp("127.0.0.3") {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[Result] = filter.apply(nextFilter)(
        request
          .withHeaders(XForwardedFor -> "127.0.0.2")
          .withHeaders(XRealIp -> "127.0.0.4")
      )

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should include("127.0.0.2")
      }
  }

  "Log an incoming request with ip address provided in remoteAddress only" in setUp("127.0.0.3") {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[Result] =
        filter.apply(nextFilter)(request.withHeaders(XRealIp -> "127.0.0.4"))

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should include("127.0.0.3")
      }
  }

  "Log an incoming request with ip address provided in XRealIp only" in setUp(missingRemoteIpAddress) {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[Result] =
        filter.apply(nextFilter)(request.withHeaders(XRealIp -> "127.0.0.4"))

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should include("127.0.0.4")
      }
  }

  "Log an incoming request with no ip address provided and no tracking id cookie" in setUp(missingRemoteIpAddress) {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[Result] = filter.apply(nextFilter)(request)

      whenReady(filterResult) { result =>
        val missingTrackingId ="[TrackingID: -]"
        val trackingIdSeparator = "-"
        val missingIpAddress = "-"
        val ipAddressSeparator = "- -"
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should startWith(missingTrackingId)
        loggerInfo should include(s"$trackingIdSeparator $missingIpAddress $ipAddressSeparator")
      }
  }

  "Log an incoming request with no content length header" in setUp(includeContentLength = false) {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>

      val filterResult: Future[Result] = filter.apply(nextFilter)(request)

      whenReady(filterResult) { result =>
        val loggerInfo = logger.captureLogInfo()
        val missingContentLength = "-"
        loggerInfo should include(s"200 $missingContentLength")
      }
  }

  "Not log request to /healthcheck" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      whenReady(filter.apply(nextFilter)(FakeRequest("GET", "http://localhost/healthcheck"))) { result =>
        Mockito.verifyNoMoreInteractions(logger.logger)
      }
  }

  "Not log request to /healthcheck if context path is set" in setUp(cp = "/test-application") {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      whenReady(filter.apply(nextFilter)(FakeRequest("GET", "http://localhost/test-application/healthcheck"))) { result =>
        Mockito.verifyNoMoreInteractions(logger.logger)
      }
  }

  "Not log request to /healthcheck with request parameters" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      whenReady(filter.apply(nextFilter)(FakeRequest("GET", "http://localhost/healthcheck?a=b&c=d"))) { result =>
        Mockito.verifyNoMoreInteractions(logger.logger)
      }
  }

  "Log request with /healthcheck/some/extra/path" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      val fakeRequest = FakeRequest("GET", "http://localhost/healthcheck/some/extra/path")
      whenReady(filter.apply(nextFilter)(fakeRequest)) { result =>
        val loggerInfo = logger.captureLogInfo()
        loggerInfo should include("http://localhost/healthcheck/some/extra/path")
      }
  }

  "Verify the no caching headers are added for text/html responses" in setUp(resultContentType = Some("text/html")) {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      whenReady(filter.apply(nextFilter)(request)) { result =>
        result.header.headers should contain(PRAGMA -> "no-cache")
        result.header.headers should contain(CACHE_CONTROL -> "no-store")
      }
  }

  "Verify the no caching headers are missing for responses whose content type is not set to text/html" in setUp() {
    case SetUp(filter, request, sessionFactory, nextFilter, logger) =>
      whenReady(filter.apply(nextFilter)(request)) { result =>
        result.header.headers should not contain (PRAGMA -> "no-cache")
        result.header.headers should not contain (CACHE_CONTROL -> "no-store")
      }
  }

  private class MockFilter(contentType: Option[String] = None) extends ((RequestHeader) => Future[Result]) {
    var passedRequest: RequestHeader = _

    override def apply(rh: RequestHeader): Future[Result] = {
      passedRequest = rh
      Future(contentType.fold[Result](Results.Ok){ ct => Results.Ok.withHeaders(CONTENT_TYPE -> ct) })
    }
  }

  private case class SetUp(filter: AccessLoggingFilter,
                           request: FakeRequest[_],
                           sessionFactory: ClientSideSessionFactory,
                           nextFilter: MockFilter,
                           logger: MockLogger)

  /**
    * The purpose of this method is to populate an instance of the SetUp case class, which is passed as an
    * argument to the test function parameter when it is invoked.
    *
    * @param ipAddress the ip address to use for a single test
    * @param cp the context path for the web app for a single test
    * @param includeContentLength whether or not to set the Content-Length HTTP header so we can test how the access
    *                             logger behaves when this is missing
    * @param resultContentType allows us to set the Content-Type HTTP header on the result object
    * @param test the function to call that contains the unit test code. Note that the function takes a
    *             SetUp case class as an argument and returns nothing (Unit)
    */
  private def setUp(ipAddress: String = "127.0.0.1",
                    cp: String = "",
                    includeContentLength: Boolean = true,
                    resultContentType: Option[String] = None)(test: SetUp => Unit) {
    val sessionFactory = mock[ClientSideSessionFactory]
    val accessLogger = new MockLogger

    class TestClfEntryBuilder extends ClfEntryBuilder {
      import uk.gov.dvla.vehicles.presentation.common.filters.AccessLoggingFilterSpec.testDate

      override def clfEntry(requestTimestamp: Date, request: RequestHeader, result: Result)(logger: LoggerLike): String = {
        val extendedResult = if (includeContentLength) result.withHeaders(CONTENT_LENGTH -> "12345") else result
        super.clfEntry(testDate, request, extendedResult)(logger)
      }
    }

    val injector = Guice.createInjector(new ScalaModule {
      override def configure(): Unit = {
        bind[ClfEntryBuilder].toInstance(new TestClfEntryBuilder())
        bind[AccessLoggingConfig].toInstance(new AccessLoggingConfig {
          override val contextPath: String = cp
        })
        bind[LoggerLike].annotatedWith(Names.named(AccessLoggerName)).toInstance(accessLogger)
      }
    })

    // Call the function argument with a new instance of the SetUp case class
    test(SetUp(
      filter = injector.getInstance(classOf[AccessLoggingFilter]),
      request = FakeRequest("GET", "/", FakeHeaders(), AnyContentAsEmpty, remoteAddress = ipAddress),
      sessionFactory = sessionFactory,
      nextFilter = new MockFilter(resultContentType),
      logger = accessLogger
    ))
  }
}

private object AccessLoggingFilterSpec {
  val testDate = new Date()
}
