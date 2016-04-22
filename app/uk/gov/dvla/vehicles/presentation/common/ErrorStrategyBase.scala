package uk.gov.dvla.vehicles.presentation.common

import com.google.inject.name.Named
import java.security.GeneralSecurityException
import play.api.LoggerLike
import play.api.libs.Codecs
import play.api.mvc.{RequestHeader, Result}
import scala.concurrent.ExecutionContext
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.InvalidSessionException
import uk.gov.dvla.vehicles.presentation.common.filters.ClfEntryBuilder
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.services.DateService

abstract class ErrorStrategyBase(clfEntryBuilder: ClfEntryBuilder,
                                           clfEntryLog: String => Unit,
                                           @Named("AccessLogger") accessLogger: LoggerLike,
                                           dateService: DateService) extends DVLALogger {
  protected def sessionExceptionResult(request: RequestHeader): Result
  protected def errorPageResult(exceptionDigest: String): Result

  def apply(request: RequestHeader, ex: Throwable)
           (implicit executionContext: ExecutionContext): Result = {
    val result = ex.getCause match {
      case _: InvalidSessionException => sessionExceptionResult(request)
      case _: GeneralSecurityException => sessionExceptionResult(request)
      case cause =>
        val exceptionDigest = Codecs.sha1(Option(cause).fold("")(c => Option(c.getMessage).getOrElse("")))
        val msg = s"Exception caught [${cause.getMessage}] " +
          s"whose digest is '$exceptionDigest' - will now display error page..."
        play.Logger.error(msg)
        errorPageResult(exceptionDigest)
    }
    clfEntryLog(clfEntryBuilder.clfEntry(dateService.now.toDate, request, result)(accessLogger))
    result
  }
}