package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import org.joda.time.format.DateTimeFormat
import org.joda.time.{LocalDate, DateTime}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

class FakePastDateServiceImpl extends DateService {

  val pastDate = LocalDate.now().minusYears(1)
  override def today = DayMonthYear(1, 1, pastDate.getYear)

  val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")

  override def now = formatter.parseDateTime("01/01/" + pastDate.getYear.toString).toInstant

  override def dateTimeISOChronology: String = new DateTime(
    1, 1, pastDate.getYear, 0, 0).toString
}
