package uk.gov.dvla.vehicles.presentation.common.mappings

import java.util.Locale
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat

object Time {

  val format = "h:mm a"
  val formatHour = "h a"

  def fromMinutes(mins: Long) = {
    val millisPerMinute = 60000
    val millis = mins * millisPerMinute
    fromHourMillis(millis)
  }

  def fromHourMillis(hourMillis: Long) =
    print(new DateTime(hourMillis, DateTimeZone.forID("UTC"))) // Must use UTC as we only want to format the hour

  def fromDateTime(date: DateTime) = {
    print(date)
  }

  def print(date: DateTime) = {
    if (date.minuteOfHour().get() == 0)
      DateTimeFormat.forPattern(formatHour).withLocale(Locale.UK)
        .print(date).toLowerCase
    else
    DateTimeFormat.forPattern(format).withLocale(Locale.UK)
      .print(date).toLowerCase
  }
}
