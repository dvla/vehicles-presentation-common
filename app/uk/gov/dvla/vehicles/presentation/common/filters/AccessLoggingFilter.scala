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
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.stringProp
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger

class AccessLoggingFilter @Inject()(clfEntryBuilder: ClfEntryBuilder,
                                    @Named("AccessLogger") accessLogger: LoggerLike,
                                    config: AccessLoggingConfig) extends Filter{

  override def apply(filter: (RequestHeader) => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    val requestTimestamp = new Date()
    filter(requestHeader).map {result =>
      val requestPath = new URI(requestHeader.uri).getPath

      if (!AccessLoggingFilter.NonLoggingUrls.map(config.contextPath + _).contains(requestPath))
        clfEntryBuilder.clfEntry(requestTimestamp, requestHeader, result)(accessLogger)
      result.header.headers.get(Http.HeaderNames.CONTENT_TYPE).fold(result) { contentType =>
        if (contentType.startsWith("text/html"))
          result.withHeaders(Http.HeaderNames.PRAGMA -> "no-cache", Http.HeaderNames.CACHE_CONTROL -> "no-store")
        else result
      }
    }
  }
}

class ClfEntryBuilder extends DVLALogger  {
  import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders._

  def clfEntry(requestTimestamp: Date, request: RequestHeader, result: Result)(logger: LoggerLike) : String = {
    val ipAddress = Seq(
      request.headers.get(XForwardedFor),
      Option(request.remoteAddress),
      request.headers.get(XRealIp),
      Some("-")
    ).flatten.head

    val trackingId = request.cookies.get(ClientSideSessionFactory.TrackingIdCookieName) match {
      case Some(c) => c.value
      case _ => "-"
    }

    val method = request.method
    val uri = request.uri
    val protocol = request.version
    val date = s"[${ClfEntryBuilder.dateFormat.format(requestTimestamp)}]"
    val responseCode = result.header.status
    val responseLength = result.header.headers.get(CONTENT_LENGTH).getOrElse("-")

    logMessage(uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId(trackingId), Info,
      s"""$ipAddress - - $date "$method $uri $protocol" $responseCode $responseLength""")(logger.logger)

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
