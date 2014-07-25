package services.dispose_service

import com.google.inject.Inject
import models.domain.disposal_of_vehicle.DisposeRequestDto
import play.api.libs.json.Json
import play.api.libs.ws.{Response, WS}
import scala.concurrent.Future
import services.HttpHeaders
import utils.helpers.Config

final class DisposeWebServiceImpl @Inject()(config: Config)  extends DisposeWebService {
  private val endPoint: String = s"${config.disposeVehicleMicroServiceBaseUrl}/vehicles/dispose/v1"
  private val requestTimeout: Int = config.disposeMsRequestTimeout

  override def callDisposeService(request: DisposeRequestDto, trackingId: String): Future[Response] =
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId)
      .withRequestTimeout(requestTimeout)
      .post(Json.toJson(request))
}
