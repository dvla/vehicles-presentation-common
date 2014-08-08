package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup

import play.api.libs.json.Json

final case class VehicleDetailsResponseDto (responseCode: Option[String], vehicleDetailsDto: Option[VehicleDetailsDto])

object VehicleDetailsResponseDto {
  implicit val JsonFormat = Json.format[VehicleDetailsResponseDto]
}
