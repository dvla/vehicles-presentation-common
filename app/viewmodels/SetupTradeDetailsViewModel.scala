package viewmodels

import models.domain.common.CacheKey
import play.api.libs.json.Json

// TODO the names of the params repeat names from the model so refactor
final case class SetupTradeDetailsViewModel(traderBusinessName: String, traderPostcode: String)

object SetupTradeDetailsViewModel {
  implicit val JsonFormat = Json.format[SetupTradeDetailsViewModel]
  final val SetupTradeDetailsCacheKey = "setupTraderDetails"
  implicit val Key = CacheKey[SetupTradeDetailsViewModel](SetupTradeDetailsCacheKey)
}
