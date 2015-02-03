package uk.gov.dvla.vehicles.presentation.common.filters

import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import com.google.inject.Inject
import com.google.inject.name.Named
import play.api.LoggerLike
import play.api.http.HeaderNames.CONTENT_LENGTH
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Filter, RequestHeader, Result}
import play.mvc.Http
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class AccessLoggingFilter @Inject()(clfEntryBuilder: ClfEntryBuilder,
                                    @Named("AccessLogger") accessLogger: LoggerLike) extends Filter {

  override def apply(filter: (RequestHeader) => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    val requestTimestamp = new Date()
    filter(requestHeader).map {result =>
      val requestPath = new URI(requestHeader.uri).getPath
      if (!AccessLoggingFilter.NonLoggingUrls.contains(requestPath))
        accessLogger.info(clfEntryBuilder.clfEntry(requestTimestamp, requestHeader, result))
      result.header.headers.get(Http.HeaderNames.CONTENT_TYPE).fold(result) { contentType =>
        if (contentType.startsWith("text/html"))
          result.withHeaders(Http.HeaderNames.PRAGMA -> "no-cache", Http.HeaderNames.CACHE_CONTROL -> "no-store")
        else result
      }
    }
  }
}

class ClfEntryBuilder {
  import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders._

  def clfEntry(requestTimestamp: Date, request: RequestHeader, result: Result) : String = {
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
    val responseLength = result.header.headers. get(CONTENT_LENGTH).getOrElse("-")

    s"""$ipAddress - - $date "$method $uri $protocol" $responseCode $responseLength "$trackingId" """
  }
}

object AccessLoggingFilter {
  final val AccessLoggerName = "AccessLogger"
  final val NonLoggingUrls = Set("/healthcheck")
}

object ClfEntryBuilder {
  val dateFormat = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss +SSS")
}
