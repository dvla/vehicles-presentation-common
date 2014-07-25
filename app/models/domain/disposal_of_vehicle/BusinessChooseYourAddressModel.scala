package models.domain.disposal_of_vehicle

import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class BusinessChooseYourAddressModel(uprnSelected: String)

object BusinessChooseYourAddressModel {
  implicit val JsonFormat = Json.format[BusinessChooseYourAddressModel]
  final val BusinessChooseYourAddressCacheKey = "businessChooseYourAddress"
  implicit val Key = CacheKey[BusinessChooseYourAddressModel](value = BusinessChooseYourAddressCacheKey)
}