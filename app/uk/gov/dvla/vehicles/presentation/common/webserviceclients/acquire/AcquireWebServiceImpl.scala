package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import com.google.inject.Inject
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.{WS, WSResponse}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import scala.concurrent.Future

final class AcquireWebServiceImpl @Inject()(config: AcquireConfig)  extends AcquireWebService {
  private val endPoint: String = s"${config.baseUrl}/vehicles/acquire/v1"
  private val requestTimeout: Int = config.requestTimeout

  override def callAcquireService(request: AcquireRequestDto, trackingId: TrackingId): Future[WSResponse] = {
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId.value)
      .withRequestTimeout(requestTimeout)
      .post(Json.toJson(request))
  }
}
