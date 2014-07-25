package models.domain.disposal_of_vehicle

import models.domain.common.{AddressAndPostcodeModel, CacheKey}
import play.api.libs.json.Json

final case class EnterAddressManuallyModel(addressAndPostcodeModel: AddressAndPostcodeModel)

object EnterAddressManuallyModel {
  final val EnterAddressManuallyCacheKey = "enterAddressManually"
  implicit val JsonFormat = Json.format[EnterAddressManuallyModel]
  implicit val Key = CacheKey[EnterAddressManuallyModel](EnterAddressManuallyCacheKey)
}