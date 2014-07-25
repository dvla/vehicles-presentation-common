package viewmodels

import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class VehicleLookupFormViewModel(referenceNumber: String,
                                        registrationNumber: String)

object VehicleLookupFormViewModel {
  implicit val JsonFormat = Json.format[VehicleLookupFormViewModel]
  final val VehicleLookupFormModelCacheKey = "vehicleLookupFormModel"
  implicit val Key = CacheKey[VehicleLookupFormViewModel](VehicleLookupFormModelCacheKey)
  final val VehicleLookupResponseCodeCacheKey = "vehicleLookupResponseCode"
}
