package services.vehicle_lookup

import play.api.libs.ws.Response
import scala.concurrent.Future

trait VehicleLookupWebService {
  def callVehicleLookupService(request: VehicleDetailsRequestDto, trackingId: String): Future[Response]
}
