package models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings

case class DateModel(optionalDate: Option[LocalDate], date: LocalDate)

object DateModel {
  implicit val Key = CacheKey[DateModel]("test")
  implicit val JsonFormat = Json.format[DateModel]

  object Form {
    final val OptionalDateId = "DateOfBirthFieldId"
    final val DateId = "DateOfBirthFieldId1"

    final val Mapping = mapping(
      OptionalDateId -> mappings.Date.optionalDateMapping,
      DateId -> mappings.Date.dateMapping
    )(DateModel.apply)(DateModel.unapply)
  }
}
