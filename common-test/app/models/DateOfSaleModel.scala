package models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings

case class DateOfSaleModel(optionalDate: Option[LocalDate], date: LocalDate)

object DateOfSaleModel {
  implicit val Key = CacheKey[DateOfSaleModel]("test")
  implicit val JsonFormat = Json.format[DateOfSaleModel]

  object Form {
    final val OptionalDateId = "DateOfSaleFieldId"
    final val DateId = "DateOfSaleFieldId1"

    final val Mapping = mapping(
      OptionalDateId -> mappings.Date.optionalDateMapping,
      DateId -> mappings.Date.dateMapping
    )(DateOfSaleModel.apply)(DateOfSaleModel.unapply)
  }
}
