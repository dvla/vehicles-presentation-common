package services.vehicle_lookup

import models.domain.disposal_of_vehicle.VehicleDetailsRequestDto
import play.api.libs.ws.Response
import scala.concurrent.Future

trait VehicleLookupWebService {
  def callVehicleLookupService(request: VehicleDetailsRequestDto, trackingId: String): Future[Response]
}
