package viewmodels

import mappings.common.{DocumentReferenceNumber, VehicleRegistrationNumber}
import models.domain.common.CacheKey
import play.api.data.Forms._
import play.api.libs.json.Json

final case class VehicleLookupFormViewModel(referenceNumber: String,
                                        registrationNumber: String)

object VehicleLookupFormViewModel {
  implicit val JsonFormat = Json.format[VehicleLookupFormViewModel]
  final val VehicleLookupFormModelCacheKey = "vehicleLookupFormModel"
  implicit val Key = CacheKey[VehicleLookupFormViewModel](VehicleLookupFormModelCacheKey)
  final val VehicleLookupResponseCodeCacheKey = "vehicleLookupResponseCode"

    object Form {
    final val DocumentReferenceNumberId = "documentReferenceNumber"
    final val VehicleRegistrationNumberId = "vehicleRegistrationNumber"

    final val Mapping = mapping(
      DocumentReferenceNumberId -> DocumentReferenceNumber.referenceNumber,
      VehicleRegistrationNumberId -> VehicleRegistrationNumber.registrationNumber
    )(VehicleLookupFormViewModel.apply)(VehicleLookupFormViewModel.unapply)
  }
}
