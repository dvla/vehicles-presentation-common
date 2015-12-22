package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import org.joda.time.{LocalDate, DateTime, Instant}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

class FakeDateServiceImpl extends DateService {
  import FakeDateServiceImpl.{DateOfDisposalDayValid, DateOfDisposalMonthValid, DateOfDisposalYearValid}

  override def today = DayMonthYear(
    DateOfDisposalDayValid.toInt,
    DateOfDisposalMonthValid.toInt,
    DateOfDisposalYearValid.toInt
  )

  override def now = Instant.now()

  override def dateTimeISOChronology: String = new DateTime(
    DateOfDisposalYearValid.toInt,
    DateOfDisposalMonthValid.toInt,
    DateOfDisposalDayValid.toInt,
    0,
    0).toString
}

object FakeDateServiceImpl {
  val today = LocalDate.now()
  final val DateOfDisposalDayValid = today.toString("dd")
  final val DateOfDisposalMonthValid = today.toString("MM")
  final val DateOfDisposalYearValid = today.toString("YYYY")
}
