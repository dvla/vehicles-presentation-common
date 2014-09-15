package models

import play.api.data.Forms.mapping
import uk.gov.dvla.vehicles.presentation.common
import common.views.models.DayMonthYear
import common.views.constraints.DayMonthYear.validDate
import uk.gov.dvla.vehicles.presentation.common.views
import views.constraints.DayMonthYear.required
import views.models

import play.api.data.Mapping
import play.api.data.Forms.{optional, number}

case class ValtechInputDayMonthYearModel(dateOfBirth: DayMonthYear)

object ValtechInputDayMonthYearModel {

  object Form {
    final val DateOfBirthId = "dateOfBirth"

    final val DayId = "day"
    final val MonthId = "month"
    final val YearId = "year"
    final val HourId = "hour"
    final val MinutesId = "minutes"
    final val SecondsId = "seconds"
    final val MillisecondsId = "milliseconds"
    final val MaxDaysInMonth = 31
    final val MaxMonthsInYear = 12
    final val MaxHoursInDay = 24
    final val MaxMinutesInHour = 59
    final val MaxSecondsInMinute = 59
    final val MaxMillisecondsInSecond = 999

    val dayMonthYear: Mapping[uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear] = mapping(
      DayId -> number(max = MaxDaysInMonth).verifying(required),
      MonthId -> number(max = MaxMonthsInYear).verifying(required),
      YearId -> number.verifying(required),
      HourId -> optional(number(min = 0, max = MaxHoursInDay)),
      MinutesId -> optional(number(min = 0, max = MaxMinutesInHour)),
      SecondsId -> optional(number(min = 0, max = MaxSecondsInMinute)),
      MillisecondsId -> optional(number(min = 0, max = MaxMillisecondsInSecond))
    )(models.DayMonthYear.apply)(models.DayMonthYear.unapply)

    final val Mapping = mapping(
      DateOfBirthId -> dayMonthYear.verifying(validDate())
    )(ValtechInputDayMonthYearModel.apply)(ValtechInputDayMonthYearModel.unapply)
  }
}
