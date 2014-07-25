package models.domain.disposal_of_vehicle

import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class VehicleDetailsModel(registrationNumber: String,
                               vehicleMake: String,
                               vehicleModel: String)

object VehicleDetailsModel {
  // Create a VehicleDetailsModel from the given VehicleDetailsDto. We do this in order get the data out of the response from micro-service call
  def fromDto(model: VehicleDetailsDto) =
    VehicleDetailsModel(
      registrationNumber = model.registrationNumber,
      vehicleMake = model.vehicleMake,
      vehicleModel = model.vehicleModel
    )

  implicit val JsonFormat = Json.format[VehicleDetailsModel]
  final val VehicleLookupDetailsCacheKey = "vehicleLookupDetails"
  implicit val Key = CacheKey[VehicleDetailsModel](VehicleLookupDetailsCacheKey)
}