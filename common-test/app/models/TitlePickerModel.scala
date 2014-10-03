package models

import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

case class TitlePickerModel(title: String)

object TitlePickerModel {

  implicit val JsonFormat = Json.format[TitlePickerModel]
  final val TitlePickerModelCacheKey = "titlePickerModel"
  implicit val Key = CacheKey[TitlePickerModel](value = TitlePickerModelCacheKey)

  object Form {
    final val TitleId = "title"

    final val Mapping = mapping(
      TitleId -> nonEmptyText
    )(TitlePickerModel.apply)(TitlePickerModel.unapply)
  }
}
