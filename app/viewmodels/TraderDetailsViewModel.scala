package viewmodels

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel

final case class TraderDetailsViewModel(traderName: String, traderAddress: AddressModel)

/**
 * Current serving as both a view-model and model. Needs splitting.
 */
object TraderDetailsViewModel {
  implicit val JsonFormat = Json.format[TraderDetailsViewModel]
  final val TraderDetailsCacheKey = "traderDetails"
  implicit val Key = CacheKey[TraderDetailsViewModel](value = TraderDetailsCacheKey)
}
