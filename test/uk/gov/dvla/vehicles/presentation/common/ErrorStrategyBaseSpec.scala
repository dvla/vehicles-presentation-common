package uk.gov.dvla.vehicles.presentation.common

import java.security.GeneralSecurityException
import java.util.Date
import org.joda.time.Instant
import org.mockito.Mockito.{verify, when}
import play.api.LoggerLike
import play.api.mvc.{Call, RequestHeader, Result}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.InvalidSessionException
import uk.gov.dvla.vehicles.presentation.common.filters.{ClfEntryBuilder, MockLogger}
import uk.gov.dvla.vehicles.presentation.common.services.DateService

class ErrorStrategyBaseSpec extends UnitSpec {
  "apply" should {
    "return session exception result in case of exceptions related to the session security" in {
      testSessionValidationException(new InvalidSessionException(""))
      testSessionValidationException(new GeneralSecurityException(""))
    }

    "return the error page result in case of exception NOT related to the session security" in {
      val (clfEntryBuilder, logger, dateService, request, errorStrategy, loggerLike) = setUp()
      // Given
      when(dateService.now).thenReturn(new Instant(100))

      val errorPageResultClfEntry = "errorPageResultClfEntry"
      when(clfEntryBuilder.clfEntry(new Date(100L), request, errorStrategy.mockErrorPageResult)(loggerLike))
        .thenReturn(errorPageResultClfEntry)

      // When
      errorStrategy.apply(request, new Exception(new Exception("some exception message"))) should equal(
        errorStrategy.mockErrorPageResult
      )

      // Then
      errorStrategy.requests should be(empty)
      verify(logger).apply(errorPageResultClfEntry)
    }
  }

  private def testSessionValidationException(t: Throwable): Unit = {
    val (clfEntryBuilder, logger, dateService, request, errorStrategy, loggerLike) = setUp()
    // Given
    when(dateService.now).thenReturn(new Instant(100))

    val sessionExceptionClfEntry = "sessionExceptionClfEntry"
    when(clfEntryBuilder.clfEntry(new Date(100L), request, errorStrategy.mockSessionExceptionResult)(loggerLike))
      .thenReturn(sessionExceptionClfEntry)

    // When
    errorStrategy.apply(request, new Exception(new InvalidSessionException(""))) should equal(
      errorStrategy.mockSessionExceptionResult
    )

    // Then
    // sessionExceptionResult is called once with the request
    errorStrategy.requests should equal(ArrayBuffer(request))
    // errorPageResult is not called
    errorStrategy.errorPageResultInvokeCount should equal(0)
    verify(logger).apply(sessionExceptionClfEntry)
  }

  class ErrorStrategyTest(clfEntryBuilder: ClfEntryBuilder,
                          logger: String => Unit,
                          loggerLike: LoggerLike,
                          dateService: DateService,
                          sessionExceptionTarget: Call,
                          errorTarget: Call)
    extends ErrorStrategyBase(clfEntryBuilder, logger, loggerLike, dateService, sessionExceptionTarget, errorTarget) {

    val mockErrorPageResult = mock[Result]
    var requests = ArrayBuffer[RequestHeader]()
    var errorPageResultInvokeCount : Int = 0

    val mockSessionExceptionResult = mock[Result]

    override protected def sessionExceptionResult(request: RequestHeader, target: Call): Result = {
      requests += request
      mockSessionExceptionResult
    }

    override protected def errorPageResult(target: Call): Result = {
      errorPageResultInvokeCount += 1
      mockErrorPageResult
    }
  }

  private def setUp() = {
    val clfEntryBuilder = mock[ClfEntryBuilder]
    val logger = mock[String => Unit]
    val dateService = mock[DateService]
    val request = mock[RequestHeader]
    val loggerLike = new MockLogger
    val mockSessionExceptionPage = mock[Call]
    val mockErrorPage = mock[Call]
    val errorStrategy = new ErrorStrategyTest(clfEntryBuilder, logger, loggerLike, dateService, mockSessionExceptionPage, mockErrorPage)
    (clfEntryBuilder, logger, dateService, request, errorStrategy, loggerLike)
  }
}
