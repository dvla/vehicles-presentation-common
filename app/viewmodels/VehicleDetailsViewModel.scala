package viewmodels

import models.domain.common.CacheKey
import models.domain.disposal_of_vehicle.VehicleDetailsDto
import play.api.libs.json.Json

final case class VehicleDetailsViewModel(registrationNumber: String,
                               vehicleMake: String,
                               vehicleModel: String)

object VehicleDetailsViewModel {
  // Create a VehicleDetailsModel from the given VehicleDetailsDto. We do this in order get the data out of the response from micro-service call
  def fromDto(model: VehicleDetailsDto) =
    VehicleDetailsViewModel(
      registrationNumber = model.registrationNumber,
      vehicleMake = model.vehicleMake,
      vehicleModel = model.vehicleModel
    )

  implicit val JsonFormat = Json.format[VehicleDetailsViewModel]
  final val VehicleLookupDetailsCacheKey = "vehicleLookupDetails"
  implicit val Key = CacheKey[VehicleDetailsViewModel](VehicleLookupDetailsCacheKey)
}
