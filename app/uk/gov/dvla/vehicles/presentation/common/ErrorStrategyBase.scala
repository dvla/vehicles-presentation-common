package uk.gov.dvla.vehicles.presentation.common

import com.google.inject.name.Named
import java.security.GeneralSecurityException
import play.api.LoggerLike
import play.api.libs.Codecs
import play.api.mvc.Results._
import play.api.mvc.{Call, RequestHeader, Result}
import scala.concurrent.ExecutionContext
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.InvalidSessionException
import uk.gov.dvla.vehicles.presentation.common.filters.ClfEntryBuilder
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CookieHelper

abstract class ErrorStrategyBase(clfEntryBuilder: ClfEntryBuilder,
                                 clfEntryLog: String => Unit,
                                 @Named("AccessLogger") accessLogger: LoggerLike,
                                 dateService: DateService,
                                 sessionExceptionTarget: Call,
                                 errorTarget: Call) extends DVLALogger {

  def apply(request: RequestHeader, ex: Throwable)
           (implicit executionContext: ExecutionContext): Result = {
    val result = ex.getCause match {
      case _: InvalidSessionException => sessionExceptionResult(request, sessionExceptionTarget)
      case _: GeneralSecurityException => sessionExceptionResult(request, sessionExceptionTarget)
      case cause =>
        val exceptionDigest = Codecs.sha1(Option(cause).fold("")(c => Option(c.getMessage).getOrElse("")))
        val msg = s"Exception caught [${cause.getMessage}] " +
          s"whose digest is '$exceptionDigest' - will now display error page..."
        play.Logger.error(msg)
        errorPageResult(errorTarget)
    }
    clfEntryLog(clfEntryBuilder.clfEntry(dateService.now.toDate, request, result)(accessLogger))
    result
  }

  protected def sessionExceptionResult(request: RequestHeader, target: Call) =
    CookieHelper.discardAllCookies(target)(request)

  protected def errorPageResult(target: Call) =
    Redirect(target)

}