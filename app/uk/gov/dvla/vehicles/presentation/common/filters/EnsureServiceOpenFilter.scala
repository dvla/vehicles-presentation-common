package uk.gov.dvla.vehicles.presentation.common.filters

import org.joda.time.{DateTime, DateTimeZone}
import play.api.mvc.{Result, Filter, RequestHeader,Results}
import uk.gov.dvla.vehicles.presentation.common.filters.ServiceOpen.whitelist
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait EnsureServiceOpenFilter extends Filter {

  protected val opening: Int
  protected val closing: Int
  protected val dateTimeZone: DateTimeZoneService
  protected val html: Appendable

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    if (whitelist.exists(requestHeader.path.contains)) nextFilter(requestHeader)
    else if (!serviceOpen()) Future(Results.Ok(html))
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
}

trait DateTimeZoneService {
  def currentDateTimeZone: DateTimeZone
}

class DateTimeZoneServiceImpl extends DateTimeZoneService {
  override def currentDateTimeZone = DateTimeZone.forID("Europe/London")
}
