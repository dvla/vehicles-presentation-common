package uk.gov.dvla.vehicles.presentation.common.services

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

object ServiceClosingWarning {

  private val LondonTimeZone = DateTimeZone.forID("Europe/London")
  private val periodFormatter = new PeriodFormatterBuilder().
    printZeroAlways().
    minimumPrintedDigits(2).
    appendHours().
    appendSeparator(":").
    appendMinutes().
    appendSeparator(".").
    appendSeconds().
    toFormatter

  def warning(closingHour: Int, closingWarnPeriodMins: Int,
              currentTime: DateTime = Instant.now.toDateTime(LondonTimeZone)): Option[String] = {

    def closingTimeInLondonToday(closingHour: Int, currentTime: DateTime): DateTime = {
      val closingTimeToday = currentTime.
        withHourOfDay(if (closingHour == 24) 0 else closingHour).
        withMinuteOfHour(0).
        withSecondOfMinute(0).
        withZone(LondonTimeZone)
      if (closingHour == 24) closingTimeToday.plusDays(1) else closingTimeToday
    }

    val closingTime = closingTimeInLondonToday(closingHour, currentTime)
    val warningTime = closingTime.minusMinutes(closingWarnPeriodMins)

    currentTime.isAfter(warningTime) && currentTime.isBefore(closingTime) match {
      case true => Some(
        new Period(currentTime, closingTime).toString(periodFormatter))
      case false => None
    }
  }
}
