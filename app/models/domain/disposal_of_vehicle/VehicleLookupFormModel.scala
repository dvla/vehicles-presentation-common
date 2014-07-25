package models.domain.disposal_of_vehicle

import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class VehicleLookupFormModel(referenceNumber: String,
                                        registrationNumber: String)

object VehicleLookupFormModel {
  implicit val JsonFormat = Json.format[VehicleLookupFormModel]
  final val VehicleLookupFormModelCacheKey = "vehicleLookupFormModel"
  implicit val Key = CacheKey[VehicleLookupFormModel](VehicleLookupFormModelCacheKey)
  final val VehicleLookupResponseCodeCacheKey = "vehicleLookupResponseCode"
}