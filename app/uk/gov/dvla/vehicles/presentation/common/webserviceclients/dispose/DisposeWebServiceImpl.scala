package uk.gov.dvla.vehicles.presentation.common.webserviceclients.dispose

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.{WSResponse, WS}
import play.api.Play.current
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.DisposeConfig
import scala.concurrent.Future

final class DisposeWebServiceImpl @Inject()(config: DisposeConfig)  extends DisposeWebService {
  private val endPoint: String = s"${config.baseUrl}/vehicles/dispose/v1"
  private val requestTimeout: Int = config.requestTimeout

  override def callDisposeService(request: DisposeRequestDto, trackingId: String): Future[WSResponse] =
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId)
      .withRequestTimeout(requestTimeout)
      .post(Json.toJson(request))
}
