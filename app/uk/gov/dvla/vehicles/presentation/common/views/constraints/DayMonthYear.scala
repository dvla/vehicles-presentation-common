package uk.gov.dvla.vehicles.presentation.common.views.constraints

import org.joda.time.DateTime
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required.RequiredField
import uk.gov.dvla.vehicles.presentation.common.views.models.{DayMonthYear => ModelsDMY}

import scala.util.Try

object DayMonthYear {
  private final val MinYear = 999
  private final val MaxYear = 9999

  def required: Constraint[Int] = Constraint[Int](RequiredField) {
    case i if i > 0 => Valid
    case _ => Invalid(ValidationError("error.dropDownInvalid")) // TODO test coverage
  }

  def validDate(minYear: Int = MinYear, maxYear: Int = MaxYear): Constraint[ModelsDMY] = {
    def isValidDateTime(dmy: ModelsDMY) = Try(new DateTime(dmy.year, dmy.month, dmy.day, 0, 0)).isSuccess
    def isWithinYearBounds(dmy: ModelsDMY) = dmy.year > minYear && dmy.year < maxYear

    Constraint(RequiredField) {
      case dmy: ModelsDMY if isValidDateTime(dmy) && isWithinYearBounds(dmy)  => Valid
      case dmy: ModelsDMY => Invalid(ValidationError("error.invalid"))
      case _ => Invalid(ValidationError("error.required")) // TODO test coverage
    }
  }

  def after(earliest: ModelsDMY): Constraint[ModelsDMY] = {
    Constraint("constraint.withinTwoYears") {
      case dmy: ModelsDMY if dmy >= earliest => Valid
      case _ => Invalid(ValidationError("error.withinTwoYears"))
    }
  }

  def notInFuture(dateService: DateService): Constraint[ModelsDMY] = {
    Constraint("constraint.notInFuture") {
      case dmy: ModelsDMY if dmy <= dateService.today => Valid
      case _ => Invalid(ValidationError("error.notInFuture"))
    }
  }
}
