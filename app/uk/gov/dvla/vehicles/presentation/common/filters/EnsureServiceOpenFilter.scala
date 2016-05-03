package uk.gov.dvla.vehicles.presentation.common.filters

import java.util.Locale
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result, Results}
import play.twirl.api.HtmlFormat
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.filters.ServiceOpen.whitelist

trait EnsureServiceOpenFilter extends Filter {

  protected val opening: Int
  protected val closing: Int
  protected val dateTimeZone: DateTimeZoneService
  private def millisInMinute = 60 * 1000L

  private def openingHourMillis = opening * millisInMinute
  private def closingHourMillis = closing * millisInMinute
  protected val html: HtmlFormat.Appendable
  protected val closedDays: List[Int] = List(7) // a list of closed days 1 to 7, Sunday by default

  protected def html(openingTime: String, closingTime: String): HtmlFormat.Appendable = html

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    if (whitelist.exists(requestHeader.path.contains)) nextFilter(requestHeader)
    else if (!serviceOpen()) Future(Results.Ok(html(h(openingHourMillis), h(closingHourMillis))))
    else nextFilter(requestHeader)
  }

  // unable to mark private due to unit test
  def serviceOpen(currentDateTime: DateTime = new DateTime(dateTimeZone.currentDateTimeZone)): Boolean = {
    val result: Boolean = isOpenToday(currentDateTime) && isDuringOpeningHours(currentDateTime.getMillisOfDay)
    Logger.trace(s"service opening times: $openingHourMillis" + s" : $closingHourMillis")
    Logger.trace(s"serviceOpen? $result")
    result
  }

  private def isOpenToday(day: DateTime): Boolean = {
    !closedDays.contains(day.getDayOfWeek)
  }

  private def isDuringOpeningHours(timeInMillis: Int): Boolean = {
    if (closingHourMillis >= openingHourMillis) (timeInMillis >= openingHourMillis) && (timeInMillis < closingHourMillis)
    else (timeInMillis >= openingHourMillis) || (timeInMillis < closingHourMillis)
  }

  private def h(hourMillis: Long) =
    DateTimeFormat.forPattern("HH:mm").withLocale(Locale.UK)
      .print(new DateTime(hourMillis, DateTimeZone.forID("UTC"))).toLowerCase // Must use UTC as we only want to format the hour
}

trait DateTimeZoneService {
  def currentDateTimeZone: DateTimeZone
}

class DateTimeZoneServiceImpl extends DateTimeZoneService {
  override def currentDateTimeZone = DateTimeZone.forID("Europe/London")
}
