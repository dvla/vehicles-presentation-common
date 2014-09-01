package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup

import play.api.libs.json.Json

final case class VehicleDetailsDto(registrationNumber: String, vehicleMake: String, vehicleModel: String, disposeFlag: Boolean)

object VehicleDetailsDto {
  implicit val JsonFormat = Json.format[VehicleDetailsDto]
}
