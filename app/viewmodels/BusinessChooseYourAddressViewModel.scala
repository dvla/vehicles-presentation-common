package viewmodels

import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class BusinessChooseYourAddressViewModel(uprnSelected: String)

object BusinessChooseYourAddressViewModel {
  implicit val JsonFormat = Json.format[BusinessChooseYourAddressViewModel]
  final val BusinessChooseYourAddressCacheKey = "businessChooseYourAddress"
  implicit val Key = CacheKey[BusinessChooseYourAddressViewModel](value = BusinessChooseYourAddressCacheKey)
}
