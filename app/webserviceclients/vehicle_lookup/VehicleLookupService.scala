package webserviceclients.vehicle_lookup

import scala.concurrent.Future

trait VehicleLookupService {
  def invoke(cmd: VehicleDetailsRequestDto, trackingId: String): (Future[(Int, Option[VehicleDetailsResponseDto])])
}
