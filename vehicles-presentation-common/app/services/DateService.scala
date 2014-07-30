package services

import org.joda.time.Instant

trait DateService {
  def today: models.DayMonthYear
  def now: Instant
  def dateTimeISOChronology: String
}