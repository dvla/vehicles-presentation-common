package models

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel

final case class TraderDetailsModel(traderName: String, traderAddress: AddressModel)

/**
 * Current serving as both a view-model and model. Needs splitting.
 */
object TraderDetailsModel {
  implicit val JsonFormat = Json.format[TraderDetailsModel]
  final val TraderDetailsCacheKey = "traderDetails"
  implicit val Key = CacheKey[TraderDetailsModel](value = TraderDetailsCacheKey)
}
