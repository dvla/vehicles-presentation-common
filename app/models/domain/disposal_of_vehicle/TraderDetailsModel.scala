package models.domain.disposal_of_vehicle

import models.domain.common.CacheKey
import play.api.libs.json.Json
import viewmodels.AddressViewModel

final case class TraderDetailsModel(traderName: String, traderAddress: AddressViewModel)

object TraderDetailsModel {
  implicit val JsonFormat = Json.format[TraderDetailsModel]
  final val TraderDetailsCacheKey = "traderDetails"
  implicit val Key = CacheKey[TraderDetailsModel](value = TraderDetailsCacheKey)
}
