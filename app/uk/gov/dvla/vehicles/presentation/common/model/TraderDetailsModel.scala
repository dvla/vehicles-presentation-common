package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

final case class TraderDetailsModel(traderName: String, traderAddress: AddressModel)

/**
 * Current serving as both a view-model and model. Needs splitting.
 */
object TraderDetailsModel {
  implicit val JsonFormat = Json.format[TraderDetailsModel]
  final val TraderDetailsCacheKey = "traderDetails"
  implicit val Key = CacheKey[TraderDetailsModel](value = TraderDetailsCacheKey)
}
