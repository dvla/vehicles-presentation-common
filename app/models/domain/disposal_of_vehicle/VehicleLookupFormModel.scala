package models.domain.disposal_of_vehicle

import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class VehicleLookupFormModel(referenceNumber: String,
                                        registrationNumber: String)

object VehicleLookupFormModel {
  final val VehicleLookupFormModelCacheKey = "vehicleLookupFormModel"
  implicit val JsonFormat = Json.format[VehicleLookupFormModel]
  implicit val Key = CacheKey[VehicleLookupFormModel](VehicleLookupFormModelCacheKey)
}