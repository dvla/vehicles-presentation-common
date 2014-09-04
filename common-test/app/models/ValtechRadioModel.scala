package models

import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

case class ValtechRadioModel(keeperType: String)

object ValtechRadioModel {

  implicit val JsonFormat = Json.format[ValtechRadioModel]
  final val ValtechRadioModelCacheKey = "valtechRadioModel"
  implicit val Key = CacheKey[ValtechRadioModel](value = ValtechRadioModelCacheKey)

  object Form {
    final val KeeperTypeId = "keeperType"

    final val Mapping = mapping(
      KeeperTypeId -> nonEmptyText
    )(ValtechRadioModel.apply)(ValtechRadioModel.unapply)

  }
}
