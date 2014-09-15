package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.{mapping, number, optional}
import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views
import views.constraints.DayMonthYear.required
import views.models

object DayMonthYear {
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
}
