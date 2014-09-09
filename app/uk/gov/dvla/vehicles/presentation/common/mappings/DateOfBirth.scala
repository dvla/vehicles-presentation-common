package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.DateTime
import play.api.data.Forms.{mapping => map, number, optional}
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required.RequiredField
import uk.gov.dvla.vehicles.presentation.common.views.models

object DateOfBirth {
  final val DayId = "day"
  final val MonthId = "month"
  final val YearId = "year"
  final val MaxDaysInMonth = 31
  final val MaxMonthsInYear = 12
  final val OptionalDateOfBirth = "optional.date.of.birth"

  val requiredDateOfBirth: Mapping[models.DateOfBirth] = mapping(constraint(RequiredField))

  val optionalDateOfBirth: Mapping[Option[models.DateOfBirth]] = optional(mapping(constraint(OptionalDateOfBirth)))

  private def mapping(constraint: Constraint[models.DateOfBirth] ) = map(
    DayId -> number(min = 1, max = MaxDaysInMonth),
    MonthId -> number(min = 1, max = MaxMonthsInYear),
    YearId -> number(min = 1)
  )(models.DateOfBirth.apply)(models.DateOfBirth.unapply)
    .verifying(constraint)

  private def constraint(name: String) = Constraint[models.DateOfBirth](name) {
    case models.DateOfBirth(day, month, year) =>
      if (new DateTime(year, month, day, 0, 0).toDate.getTime >=
        new DateTime().plusDays(1).withTimeAtStartOfDay().toDate.getTime)
        Invalid(ValidationError(Messages("dateOfBirthInput.future")))
      else Valid
    case _ => Invalid(ValidationError("error.dropDownInvalid"))
  }
}
