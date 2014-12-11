package uk.gov.dvla.vehicles.presentation.common.filters

import java.nio.channels.WritableByteChannel
import java.util.Locale
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import play.api.mvc.{Filter, RequestHeader, Result, Results}
import play.twirl.api.{Html, Txt, HtmlFormat}
import uk.gov.dvla.vehicles.presentation.common.filters.ServiceOpen.whitelist
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EnsureServiceOpenFilter extends Filter {

  protected val opening: Int
  protected val closing: Int
  protected val dateTimeZone: DateTimeZoneService
  protected val html: HtmlFormat.Appendable

  protected def html(openingTime: String, closingTime: String): HtmlFormat.Appendable = html

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    if (whitelist.exists(requestHeader.path.contains)) nextFilter(requestHeader)
    else if (!serviceOpen()) Future(Results.Ok(html(h(opening), h(closing))))
    else nextFilter(requestHeader)
  }

  def serviceOpen(currentDateTime: DateTime = new DateTime(dateTimeZone.currentDateTimeZone)): Boolean = {
    isNotSunday(currentDateTime) && isDuringOpeningHours(currentDateTime.getMillisOfDay)
  }

  def isNotSunday(day: DateTime): Boolean = day.getDayOfWeek != 7

  def isDuringOpeningHours(timeInMillis: Int): Boolean = {
    if (closing >= opening) (timeInMillis >= opening) && (timeInMillis < closing)
    else (timeInMillis >= opening) || (timeInMillis < closing)
  }

  private def h(hour: Long) =
    DateTimeFormat.forPattern("HH:mm").withLocale(Locale.UK)
      .print(new DateTime(hour * 3600000, DateTimeZone.forID("UTC"))).toLowerCase // Must use UTC as we only want to format the hour
}

trait DateTimeZoneService {
  def currentDateTimeZone: DateTimeZone
}

class DateTimeZoneServiceImpl extends DateTimeZoneService {
  override def currentDateTimeZone = DateTimeZone.forID("Europe/London")
}
