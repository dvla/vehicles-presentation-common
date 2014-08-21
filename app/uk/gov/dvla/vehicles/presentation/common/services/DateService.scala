package uk.gov.dvla.vehicles.presentation.common.services

import org.joda.time.Instant
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

trait DateService {
  def today: DayMonthYear
  def now: Instant
  def dateTimeISOChronology: String
}
