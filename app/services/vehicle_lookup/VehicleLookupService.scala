package services.vehicle_lookup

import models.domain.disposal_of_vehicle.{VehicleDetailsRequestDto, VehicleDetailsResponseDto}
import scala.concurrent.Future

trait VehicleLookupService {
  def invoke(cmd: VehicleDetailsRequestDto, trackingId: String): (Future[(Int, Option[VehicleDetailsResponseDto])])
}
