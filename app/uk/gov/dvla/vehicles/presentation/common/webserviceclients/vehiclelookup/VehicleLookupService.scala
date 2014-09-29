package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup

import scala.concurrent.Future

trait VehicleLookupService {
  def invoke(cmd: VehicleDetailsRequestDto, trackingId: String): Future[VehicleDetailsResponseDto]
}
