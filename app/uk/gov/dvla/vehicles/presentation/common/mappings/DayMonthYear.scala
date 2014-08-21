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
  final val MaxDaysInMonth = 31
  final val MaxMonthsInYear = 12
  final val MaxHoursInDay = 24
  final val MaxMinutesInHour = 59

  val dayMonthYear: Mapping[uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear] = mapping(
    DayId -> number(max = MaxDaysInMonth).verifying(required),
    MonthId -> number(max = MaxMonthsInYear).verifying(required),
    YearId -> number.verifying(required),
    HourId -> optional(number(min = 0, max = MaxHoursInDay)),
    MinutesId -> optional(number(min = 0, max = MaxMinutesInHour))
  )(models.DayMonthYear.apply)(models.DayMonthYear.unapply)
}
