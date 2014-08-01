package webserviceclients.dispose_service

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.{Response, WS}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import utils.helpers.Config

import scala.concurrent.Future

final class DisposeWebServiceImpl @Inject()(config: Config)  extends DisposeWebService {
  private val endPoint: String = s"${config.disposeVehicleMicroServiceBaseUrl}/vehicles/dispose/v1"
  private val requestTimeout: Int = config.disposeMsRequestTimeout

  override def callDisposeService(request: DisposeRequestDto, trackingId: String): Future[Response] =
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId)
      .withRequestTimeout(requestTimeout)
      .post(Json.toJson(request))
}
