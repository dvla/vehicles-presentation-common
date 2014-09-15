package models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings

case class DateOfBirthModel(dateOfBirth: Option[LocalDate], dateOfBirth1: LocalDate)

object DateOfBirthModel {
  implicit val Key = CacheKey[DateOfBirthModel]("test")
  implicit val JsonFormat = Json.format[DateOfBirthModel]

  object Form {
    final val DateOfBirthDayId = "DateOfBirthFieldId"
    final val DateOfBirthDayId1 = "DateOfBirthFieldId1"

    final val Mapping = mapping(
      DateOfBirthDayId -> mappings.DateOfBirth.optionalMapping,
      DateOfBirthDayId1 -> mappings.DateOfBirth.mapping
    )(DateOfBirthModel.apply)(DateOfBirthModel.unapply)
  }
}
