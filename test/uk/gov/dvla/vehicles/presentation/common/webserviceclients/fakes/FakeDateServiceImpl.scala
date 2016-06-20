package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import org.joda.time.{LocalDate, DateTime, Instant}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

class FakeDateServiceImpl extends DateService {
  import FakeDateServiceImpl.{DateDayValid, DateMonthValid, DateYearValid}

  override def today = DayMonthYear(
    DateDayValid.toInt,
    DateMonthValid.toInt,
    DateYearValid.toInt
  )

  override def now = Instant.now()

  override def dateTimeISOChronology: String = new DateTime(
    DateYearValid.toInt,
    DateMonthValid.toInt,
    DateDayValid.toInt,
    0,
    0).toString
}

object FakeDateServiceImpl {
  val today = LocalDate.now()
  final val DateDayValid = today.toString("dd")
  final val DateMonthValid = today.toString("MM")
  final val DateYearValid = today.toString("YYYY")
}
