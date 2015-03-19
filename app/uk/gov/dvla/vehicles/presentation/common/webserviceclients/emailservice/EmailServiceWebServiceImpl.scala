package webserviceclients.emailservice

import com.google.inject.Inject
import play.api.Logger
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.{WS, WSResponse}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.EmailServiceConfig

import scala.concurrent.Future

final class EmailServiceWebServiceImpl @Inject()(config: EmailServiceConfig) extends EmailServiceWebService {

  override def invoke(request: EmailServiceSendRequest, trackingId: String): Future[WSResponse] = {

    val endPoint: String = s"${config.emailServiceMicroServiceBaseUrl}/email/send"

    Logger.debug(endPoint)
    Logger.debug(s"Calling email service micro-service with request with tracking id: $trackingId")
    WS.url(endPoint).
      withHeaders(HttpHeaders.TrackingId -> trackingId).
      withRequestTimeout(config.requestTimeout). // Timeout is in milliseconds
      post(Json.toJson(request))
  }

}