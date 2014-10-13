package models

import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.{TitleType, TitlePickerString}

case class TitlePickerModel(title: TitleType)

object TitlePickerModel {
  implicit val JsonFormatTitleType = Json.format[TitleType]
  implicit val JsonFormat = Json.format[TitlePickerModel]
  final val TitlePickerModelCacheKey = "titlePickerModel"
  implicit val Key = CacheKey[TitlePickerModel](value = TitlePickerModelCacheKey)

  object Form {
    final val TitleId = "title"

    final val Mapping = mapping(
      TitleId -> TitlePickerString.mapping
    )(TitlePickerModel.apply)(TitlePickerModel.unapply)
  }
}
