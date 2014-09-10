package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.FormError
import play.api.data.Forms.{of, optional}
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required
import scala.util.Try

object DateOfBirth {
  final val DayId = "day"
  final val MonthId = "month"
  final val YearId = "year"
  final val MaxDaysInMonth = 31
  final val MaxMonthsInYear = 12
  final val OptionalDateOfBirth = "optional.date.of.birth"

  val formatter = new Formatter[LocalDate] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
      val dateOfBirth: Option[LocalDate] = for {
        dayText <- data.get(s"$key.$DayId")
        monthText <- data.get(s"$key.$MonthId")
        yearText <- data.get(s"$key.$YearId")
        day <- Try(dayText.toInt).toOption
        month <- Try(monthText.toInt).toOption
        year <- Try(yearText.toInt).toOption
        dateOfBirth <- Try(new LocalDate(year, month, day)).toOption
      } yield dateOfBirth
      dateOfBirth.map(Right(_)).getOrElse{
        Left(Seq[FormError](FormError(key, "error.dateOfBirth.invalid")))
      }
    }

    def unbind(key: String, value: LocalDate) = ???
  }

  val mapping = of[LocalDate](formatter) verifying constraint(Required.RequiredField)

  val optionalMapping = optional(of[LocalDate](formatter) verifying constraint("Optional DateOfBirth"))

  def constraint(name: String) = Constraint[LocalDate](name) {
    case d: LocalDate =>
      if (d.isAfter(LocalDate.now)) Invalid(ValidationError(Messages("error.dateOfBirth.inTheFuture")))
      else Valid
  }
}
