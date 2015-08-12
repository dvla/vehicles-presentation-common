package uk.gov.dvla.vehicles.presentation.common

import java.security.GeneralSecurityException

import com.google.inject.name.Named
import play.Logger
import play.api.LoggerLike
import play.api.libs.Codecs
import play.api.mvc.{RequestHeader, Result}
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClientSideSessionFactory, InvalidSessionException}
import uk.gov.dvla.vehicles.presentation.common.filters.ClfEntryBuilder
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import common.clientsidesession.CookieImplicits.RichCookies
import scala.concurrent.ExecutionContext

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

        errorPageResult(exceptionDigest)
    }
    clfEntryLog(clfEntryBuilder.clfEntry(dateService.now.toDate, request, result)(accessLogger))
    result
  }
}