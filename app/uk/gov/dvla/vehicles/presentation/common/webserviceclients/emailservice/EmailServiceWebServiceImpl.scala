package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.{WS, WSResponse}
import play.api.Play.current
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders

final class EmailServiceWebServiceImpl @Inject()(config: EmailServiceConfig)
    extends EmailServiceWebService with DVLALogger {

  override def invoke(request: EmailServiceSendRequest, trackingId: TrackingId): Future[WSResponse] = {

    val endPoint: String = s"${config.emailServiceMicroServiceBaseUrl}/email/send"

    logMessage(trackingId, Debug, "Calling email service micro-service with request")
    WS.url(endPoint).
      withHeaders(HttpHeaders.TrackingId -> trackingId.value).
      withRequestTimeout(config.requestTimeout). // Timeout is in milliseconds
      post(Json.toJson(request))
  }

}