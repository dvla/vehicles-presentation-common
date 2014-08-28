package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup.VehicleDetailsDto

final case class VehicleDetailsModel(registrationNumber: String,
                               vehicleMake: String,
                               vehicleModel: String,
                               disposeFlag: Boolean)

object VehicleDetailsModel {
  // Create a VehicleDetailsModel from the given VehicleDetailsDto. We do this in order get the data out of the response from micro-service call
  def fromDto(model: VehicleDetailsDto): VehicleDetailsModel =
    VehicleDetailsModel(
      registrationNumber = model.registrationNumber,
      vehicleMake = model.vehicleMake,
      vehicleModel = model.vehicleModel,
      disposeFlag = model.disposeFlag
    )

  implicit val JsonFormat = Json.format[VehicleDetailsModel]
  final val VehicleLookupDetailsCacheKey = "vehicleLookupDetails"
  implicit val Key = CacheKey[VehicleDetailsModel](VehicleLookupDetailsCacheKey)
}
