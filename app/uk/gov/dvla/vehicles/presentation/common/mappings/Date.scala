package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.FormError
import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.Messages
import scala.util.Try
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required

object Date {
  final val DayId = "day"
  final val MonthId = "month"
  final val YearId = "year"
  final val TodaysDateId = "todays_date"
  final val MaxDaysInMonth = 31
  final val MaxMonthsInYear = 12

  def formatter(errorMessageKey: String = "error.date.invalid") = new Formatter[LocalDate] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
      val date: Option[LocalDate] = for {
        dayText <- data.get(s"$key.$DayId")
        monthText <- data.get(s"$key.$MonthId")
        yearText <- data.get(s"$key.$YearId")
        dayTwoDigits <- datePart(dayText, 2)
        monthTwoDigits <- datePart(monthText, 2)
        yearFourDigits <- datePart(yearText, 4)
        day <- toInt(dayText)
        month <- toInt(monthText)
        year <- toInt(yearText)
        date <- createDate(year, month, day)
      } yield date
      date.toRight(Seq[FormError](FormError(key, errorMessageKey)))
    }

    private def datePart(datePartText: String, length: Int): Option[String] = {
      if (datePartText.length == length) Some(datePartText) else None
    }

    private def toInt(datePartText: String): Option[Int] = {
      Try(datePartText.toInt).toOption
    }

    private def createDate(year: Int, month: Int, day: Int): Option[LocalDate] = {
      Try(new LocalDate(year, month, day)).toOption
    }

    def unbind(key: String, value: LocalDate) = Map(
      s"$key.$DayId" -> value.toString("dd"),
      s"$key.$MonthId" -> value.toString("MM"),
      s"$key.$YearId" -> value.toString("YYYY")
    )
  }

  val dateMapping = of[LocalDate](formatter()) verifying required

  def required = Constraint[LocalDate](Required.RequiredField) {
    case _ => Valid
  }

  private def notAfter(notAfter: LocalDate,
               message: String = Messages("error.date.notAfter"),
               name: String = "constraint.notAfter") = Constraint[LocalDate](name) {
    case d: LocalDate =>
      if (d.isAfter(notAfter)) Invalid(ValidationError(message))
      else Valid
  }

  def notInTheFuture(message: String = Messages("error.date.inTheFuture"),
                     name: String = "constraint.notInTheFuture")(implicit dateService: DateService) =
    notAfter(dateService.now.toDateTime.toLocalDate, message, name)

  def notBefore(date: LocalDate,
                message: String = Messages("error.date.notBefore"),
                name: String = "constraint.notBefore" ) = Constraint[LocalDate](name) {
    case d: LocalDate =>
      if (d.isBefore(date)) Invalid(ValidationError(message))
      else Valid
  }

  // TODO - remove this as it is not used by exemplars/widget (only unit test)
  def notInThePast(message: String = Messages("error.date.notInThePast"),
                   name: String = "constraint.notInThePast")(implicit dateService: DateService) =
    notBefore(dateService.now.toDateTime.toLocalDate, message, name)
}
