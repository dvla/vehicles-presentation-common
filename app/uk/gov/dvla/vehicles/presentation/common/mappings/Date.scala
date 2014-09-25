package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.FormError
import play.api.data.Forms.{of, optional}
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.Messages
import play.api.i18n.Messages.Message
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required
import scala.util.Try
import com.github.nscala_time.time.RichLocalDate

object Date {
  final val DayId = "day"
  final val MonthId = "month"
  final val YearId = "year"
  final val MaxDaysInMonth = 31
  final val MaxMonthsInYear = 12
  final val OptionalDateOfBirth = "optional.date"
  final val ValidYearsAgo = 110

  def formatter(errorMessageKey: String = "error.date.invalid") = new Formatter[LocalDate] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
      val dateOfBirth: Option[LocalDate] = for {
        dayText <- data.get(s"$key.$DayId")
        monthText <- data.get(s"$key.$MonthId")
        yearText <- data.get(s"$key.$YearId")
        yearFourDigits <- if (yearText.length == 4) Some(yearText) else None
        day <- Try(dayText.toInt).toOption
        month <- Try(monthText.toInt).toOption
        year <- Try(yearText.toInt).toOption
        dateOfBirth <- Try(new LocalDate(year, month, day)).toOption
      } yield dateOfBirth
      dateOfBirth.toRight(Seq[FormError](FormError(key, errorMessageKey)))
    }

    def unbind(key: String, value: LocalDate) = Map(
      s"$key.$DayId" -> value.getDayOfMonth.toString,
      s"$key.$MonthId" -> value.getMonthOfYear.toString,
      s"$key.$YearId" -> value.getYear.toString
    )
  }

  val dateMapping = of[LocalDate](formatter()) verifying required

  val optionalDateMapping = optional(of[LocalDate](formatter()))

  val optionalNonFutureDateMapping = optional(of[LocalDate](formatter()) verifying notInTheFuture())

  private val genericDateOfBirth = of[LocalDate](formatter("error.dateOfBirth.invalid"))
    .verifying(notInTheFuture(Messages("error.dateOfBirth.inTheFuture")))
    .verifying(notBefore(LocalDate.now.minusYears(110), Messages("error.dateOfBirth.110yearsInThePast")))

  val dateOfBirth = genericDateOfBirth verifying required

  val optionalDateOfBirth = optional(genericDateOfBirth)

  def required = Constraint[LocalDate](Required.RequiredField) {
    case _ => Valid
  }

  def notAfter(notAfter: LocalDate,
               message: String = Messages("error.date.notAfter"),
               name: String = "constraint.notAfter") = Constraint[LocalDate](name) {
    case d: LocalDate =>
      if (d.isAfter(notAfter)) Invalid(ValidationError(message))
      else Valid
  }

  def notInTheFuture(message: String = Messages("error.date.inTheFuture"),
                     name: String = "constraint.notInTheFuture") = notAfter(LocalDate.now, message, name)

  def notBefore(date: LocalDate,
                message: String = Messages("error.date.notBefore"),
                name: String = "constraint.notBefore" ) = Constraint[LocalDate](name) {
    case d: LocalDate =>
      if (d.isBefore(date)) Invalid(ValidationError(message))
      else Valid
  }

  def notInThePast(message: String = Messages("error.date.notInThePast"),
                     name: String = "constraint.notInThePast") = notBefore(LocalDate.now, message, name)
}
