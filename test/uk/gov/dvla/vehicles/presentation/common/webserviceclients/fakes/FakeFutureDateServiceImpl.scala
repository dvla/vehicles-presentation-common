package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

class FakeFutureDateServiceImpl extends DateService {

  override def today = DayMonthYear(1, 1, 3001)

  val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")

  override def now = formatter.parseDateTime("01/01/3001").toInstant

  override def dateTimeISOChronology: String = new DateTime(
    1, 1, 3001, 0, 0).toString
}
