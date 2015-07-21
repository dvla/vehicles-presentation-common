package uk.gov.dvla.vehicles.presentation.common

import java.security.GeneralSecurityException

import play.Logger
import play.api.libs.Codecs
import play.api.mvc.{RequestHeader, Result}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.InvalidSessionException
import uk.gov.dvla.vehicles.presentation.common.filters.ClfEntryBuilder
import uk.gov.dvla.vehicles.presentation.common.services.DateService

import scala.concurrent.ExecutionContext

abstract class ErrorStrategyBase(clfEntryBuilder: ClfEntryBuilder,
                                           clfEntryLog: String => Unit,
                                           dateService: DateService) {
  protected def sessionExceptionResult(request: RequestHeader): Result
  protected def errorPageResult(exceptionDigest: String): Result

  def apply(request: RequestHeader, ex: Throwable)
           (implicit executionContext: ExecutionContext): Result = {
    val result = ex.getCause match {
      case _: InvalidSessionException => sessionExceptionResult(request)
      case _: GeneralSecurityException => sessionExceptionResult(request)
      case cause =>
        val exceptionDigest = Codecs.sha1(Option(cause).fold("")(c => Option(c.getMessage).getOrElse("")))
        Logger.error(s"Exception thrown with digest '$exceptionDigest'", cause)
        errorPageResult(exceptionDigest)
    }
    clfEntryLog(clfEntryBuilder.clfEntry(dateService.now.toDate, request, result))
    result
  }
}