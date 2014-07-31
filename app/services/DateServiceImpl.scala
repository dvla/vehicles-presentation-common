package services

import models.DayMonthYear
import org.joda.time.{DateTime, Instant}
import uk.gov.dvla.vehicles.presentation.common.services.DateService

final class DateServiceImpl extends DateService {
  override def today = DayMonthYear.today
  override def now = Instant.now()
  override def dateTimeISOChronology: String = DateTime.now().toString
}