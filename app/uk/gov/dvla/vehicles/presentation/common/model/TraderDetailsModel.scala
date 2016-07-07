package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

final case class TraderDetailsModel(traderName: String, traderAddress: AddressModel, traderEmail: Option[String])

// TODO refactor into view-model and model
object TraderDetailsModel {
  implicit val JsonFormat = Json.format[TraderDetailsModel]

  implicit def key(implicit prefix: CacheKeyPrefix): CacheKey[TraderDetailsModel] =
    CacheKey[TraderDetailsModel](value = traderDetailsCacheKey)

  def traderDetailsCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}traderDetails"
}
