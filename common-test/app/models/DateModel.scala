package models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings

case class DateModel(date: Option[LocalDate], optionalDate: LocalDate)

object DateModel {
  implicit val Key = CacheKey[DateModel]("test")
  implicit val JsonFormat = Json.format[DateModel]

  object Form {
    final val DateId = "DateOfBirthFieldId"
    final val OptionalDateId = "DateOfBirthFieldId1"

    final val Mapping = mapping(
      DateId -> mappings.Date.optionalNonFutureDateMapping,
      OptionalDateId -> mappings.Date.nonFutureDateMapping
    )(DateModel.apply)(DateModel.unapply)
  }
}
