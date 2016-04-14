package uk.gov.dvla.vehicles.presentation.common.filters

import com.google.inject.Inject
import com.google.inject.name.Named
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import play.api.http.HeaderNames.CONTENT_LENGTH
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.LoggerLike
import play.api.mvc.{Filter, RequestHeader, Result}
import play.mvc.Http
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.stringProp
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger

class AccessLoggingFilter @Inject()(clfEntryBuilder: ClfEntryBuilder,
                                    @Named("AccessLogger") accessLogger: LoggerLike,
                                    config: AccessLoggingConfig) extends Filter {

  override def apply(filter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    def shouldLogRequest(requestPath: String): Boolean =
      // The request path is not contained in the set of non-logging-urls
      !AccessLoggingFilter.NonLoggingUrls.map(config.contextPath + _).contains(requestPath)

    def disableBrowserCachingForTextHtmlResponses(result: Result): Result =
      result.header.headers.get(Http.HeaderNames.CONTENT_TYPE).fold(result) { contentType =>
        if (contentType.startsWith("text/html"))
          result.withHeaders(Http.HeaderNames.PRAGMA -> "no-cache", Http.HeaderNames.CACHE_CONTROL -> "no-store")
        else result
      }

    filter(requestHeader).map { result =>
      val requestTimestamp = new Date()
      val requestPath = new URI(requestHeader.uri).getPath

      if (shouldLogRequest(requestPath))
        clfEntryBuilder.clfEntry(requestTimestamp, requestHeader, result)(accessLogger)

      disableBrowserCachingForTextHtmlResponses(result)
    }
  }
}

class ClfEntryBuilder extends DVLALogger  {
  import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders.{XForwardedFor, XRealIp}

  def clfEntry(requestTimestamp: Date, request: RequestHeader, result: Result)(accessLogger: LoggerLike): String = {

    val missing = "-"

    def getTrackingIdOutOfTrackingIdCookie = request.cookies.get(ClientSideSessionFactory.TrackingIdCookieName)
      .fold(missing) { cookie => cookie.value }

    val ipAddress = Seq(
      request.headers.get(XForwardedFor),
      Option(request.remoteAddress),
      request.headers.get(XRealIp),
      Some(missing)
    ).flatten.head

    val trackingId = getTrackingIdOutOfTrackingIdCookie
    val method = request.method
    val uri = request.uri
    val protocol = request.version
    val date = s"[${ClfEntryBuilder.dateFormat.format(requestTimestamp)}]"
    val responseCode = result.header.status
    val responseLength = result.header.headers.getOrElse(CONTENT_LENGTH, missing)

    logMessage(TrackingId(trackingId), Info,
      s"""$ipAddress - - $date "$method $uri $protocol" $responseCode $responseLength""")(accessLogger.logger)

    s"""$ipAddress - - $date "$method $uri $protocol" $responseCode $responseLength "$trackingId" """
  }
}

trait AccessLoggingConfig {
  val contextPath: String
}

class DefaultAccessLoggingConfig extends AccessLoggingConfig {
  override val contextPath: String = getOptionalProperty[String]("application.context").getOrElse("")
}

object AccessLoggingFilter {
  final val AccessLoggerName = "AccessLogger"
  final val NonLoggingUrls = Set("/healthcheck")
}

object ClfEntryBuilder {
  val dateFormat = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss +SSS")
}
