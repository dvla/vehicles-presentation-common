package models

import play.api.data.Forms.mapping
import uk.gov.dvla.vehicles.presentation.common.mappings
import uk.gov.dvla.vehicles.presentation.common.views.models.DateOfBirth

case class DateOfBirthModel(dateOfBirth: Option[DateOfBirth], dateOfBirth1: DateOfBirth)

object DateOfBirthModel {
  object Form {
    final val DateOfBirthDayId = "DateOfBirthFieldId"
    final val DateOfBirthDayId1 = "DateOfBirthFieldId1"

    final val Mapping = mapping(
      DateOfBirthDayId -> mappings.DateOfBirth.optionalDateOfBirth,
      DateOfBirthDayId1 -> mappings.DateOfBirth.requiredDateOfBirth
    )(DateOfBirthModel.apply)(DateOfBirthModel.unapply)
  }
}