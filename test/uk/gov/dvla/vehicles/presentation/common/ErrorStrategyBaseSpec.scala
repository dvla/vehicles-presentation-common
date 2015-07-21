package uk.gov.dvla.vehicles.presentation.common

import java.security.GeneralSecurityException
import java.util.Date

import org.joda.time.Instant
import play.api.libs.Codecs
import play.api.mvc.{RequestHeader, Result}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.InvalidSessionException
import uk.gov.dvla.vehicles.presentation.common.filters.ClfEntryBuilder
import uk.gov.dvla.vehicles.presentation.common.services.DateService

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import org.mockito.Mockito.{verify, when}

class ErrorStrategyBaseSpec extends UnitSpec {
  "apply" should {
    "return session exception result in case of exceptions related to the session security" in {
      testSessionValidationException(new InvalidSessionException(""))
      testSessionValidationException(new GeneralSecurityException(""))
    }

    "return the error page result in case of exception NOT related to the session security" in {
      val (clfEntryBuilder, logger, dateService, request, errorStrategy) = setUp
      // Given
      when(dateService.now).thenReturn(new Instant(100))

      val errorPageResultClfEntry = "errorPageResultClfEntry"
      when(clfEntryBuilder.clfEntry(new Date(100L), request, errorStrategy.mockErrorPageResult))
        .thenReturn(errorPageResultClfEntry)

      // When
      errorStrategy.apply(request, new Exception(new Exception("some exception message"))) should equal(
        errorStrategy.mockErrorPageResult
      )

      // Then
      errorStrategy.requests should be(empty)
      errorStrategy.exceptionDigests should equal(
        ArrayBuffer(Codecs.sha1("some exception message"))
      )
      verify(logger).apply(errorPageResultClfEntry)
    }
  }

  private def testSessionValidationException(t: Throwable): Unit = {
    val (clfEntryBuilder, logger, dateService, request, errorStrategy) = setUp
    // Given
    when(dateService.now).thenReturn(new Instant(100))

    val sessionExceptionClfEntry = "sessionExceptionClfEntry"
    when(clfEntryBuilder.clfEntry(new Date(100L), request, errorStrategy.mockSessionExceptionResult))
      .thenReturn(sessionExceptionClfEntry)

    // When
    errorStrategy.apply(request, new Exception(new InvalidSessionException(""))) should equal(
      errorStrategy.mockSessionExceptionResult
    )

    // Then
    errorStrategy.requests should equal(ArrayBuffer(request))
    errorStrategy.exceptionDigests should be(empty)
    verify(logger).apply(sessionExceptionClfEntry)
  }

  class ErrorStrategyTest(clfEntryBuilder: ClfEntryBuilder,
                          logger: String => Unit,
                          dateService: DateService)
    extends ErrorStrategyBase(clfEntryBuilder, logger, dateService) {

    val mockErrorPageResult = mock[Result]
    var requests = ArrayBuffer[RequestHeader]()
    var exceptionDigests = ArrayBuffer[String]()
    val mockSessionExceptionResult = mock[Result]

    override protected def sessionExceptionResult(request: RequestHeader): Result = {
      requests += request
      mockSessionExceptionResult
    }

    override protected def errorPageResult(exceptionDigest: String): Result = {
      exceptionDigests += exceptionDigest
      mockErrorPageResult
    }
  }

  def setUp = {
    val clfEntryBuilder = mock[ClfEntryBuilder]
    val logger = mock[String => Unit]
    val dateService = mock[DateService]
    val request = mock[RequestHeader]
    val errorStrategy = new ErrorStrategyTest(clfEntryBuilder, logger, dateService)
    (clfEntryBuilder, logger, dateService, request, errorStrategy)
  }
}
