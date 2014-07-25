package services.vehicle_lookup

import play.api.libs.json.Json

final case class VehicleDetailsResponseDto (responseCode: Option[String], vehicleDetailsDto: Option[VehicleDetailsDto])

object VehicleDetailsResponseDto {
  implicit val JsonFormat = Json.format[VehicleDetailsResponseDto]
}
