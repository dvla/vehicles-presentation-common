package viewmodels

import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class TraderDetailsViewModel(traderName: String, traderAddress: AddressViewModel)

object TraderDetailsViewModel {
  implicit val JsonFormat = Json.format[TraderDetailsViewModel]
  final val TraderDetailsCacheKey = "traderDetails"
  implicit val Key = CacheKey[TraderDetailsViewModel](value = TraderDetailsCacheKey)
}
