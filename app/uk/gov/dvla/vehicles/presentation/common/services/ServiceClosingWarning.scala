package uk.gov.dvla.vehicles.presentation.common.services

import java.util.Locale
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.{DateTimeFormat, PeriodFormatterBuilder}
import org.joda.time.Instant
import org.joda.time.Period

object ServiceClosingWarning {

  private val LondonTimeZone = DateTimeZone.forID("Europe/London")
  private val periodFormatter = new PeriodFormatterBuilder()
    .printZeroAlways()
    .minimumPrintedDigits(2)
    .appendHours()
    .appendSeparator(":")
    .appendMinutes()
    .appendSeparator(".")
    .appendSeconds()
    .toFormatter

  def formatMinutes(mins: Long) = {
    val millisPerMinute = 60000
    val millis = mins * millisPerMinute
    DateTimeFormat.forPattern("HH:mm").withLocale(Locale.UK)
      .print(new DateTime(millis, DateTimeZone.forID("UTC"))).toLowerCase // Must use UTC as we only want to format the hour
  }

  def warning(closingMins: Int, closingWarnPeriodMins: Int,
              currentTime: DateTime = Instant.now.toDateTime(LondonTimeZone)): Option[String] = {

    def closingTimeInLondonToday(closingMins: Int, currentTime: DateTime): DateTime = {
      val millisPerHour = 60000
      val endOfDayMins = 1440
      val startOfDayMillis = 0

      val millisOfDay = closingMins match {
        case 1440 => startOfDayMillis
        case _ => closingMins * millisPerHour
      }

      val closingTimeToday = currentTime.
        withMillisOfDay(millisOfDay).
        withZone(LondonTimeZone)
      if (closingMins == endOfDayMins) closingTimeToday.plusDays(1) else closingTimeToday
    }

    val closingTime = closingTimeInLondonToday(closingMins, currentTime)
    val warningTime = closingTime.minusMinutes(closingWarnPeriodMins)

    currentTime.isAfter(warningTime) && currentTime.isBefore(closingTime) match {
      case true => Some(new Period(currentTime, closingTime).toString(periodFormatter))
      case false => None
    }
  }
}
