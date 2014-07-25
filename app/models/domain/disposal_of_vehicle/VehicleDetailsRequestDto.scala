package models.domain.disposal_of_vehicle

import play.api.libs.json.Json

final case class VehicleDetailsRequestDto(referenceNumber: String,
                                       registrationNumber: String,
                                       userName: String)

object VehicleDetailsRequestDto {
  implicit val JsonFormat = Json.format[VehicleDetailsRequestDto]
}
