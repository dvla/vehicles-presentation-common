package uk.gov.dvla.vehicles.presentation.common.views.widgets

import org.joda.time.Instant
import org.mockito.Mockito.when
import play.api.i18n.Lang
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.html.widgets.serviceClosingWarning

class ServiceClosingWarningSpec extends UnitSpec {
  "Show x minutes before closing" in {
    val dateService = mock[DateService]
    when(dateService.now).thenReturn(minutesAfter0(10))
    serviceClosingWarning(1, 51, dateService)(Lang("en", "en"))
      .toString should include("global.serviceCloseWarning")
  }

  "Not show after closing time" in {
    val dateService = mock[DateService]
    when(dateService.now).thenReturn(minutesAfter0(61))
    serviceClosingWarning(1, 59, dateService)(Lang("en", "en"))
      .toString should not include("global.serviceCloseWarning")
  }

  "Not show earlier then x minutes before closing" in {
    val dateService = mock[DateService]
    when(dateService.now).thenReturn(minutesAfter0(10))
    serviceClosingWarning(1, 49, dateService)(Lang("en", "en"))
      .toString should not include("global.serviceCloseWarning")
  }

  "Show warning for the 24 hours case" in {
    val dateService = mock[DateService]
    when(dateService.now).thenReturn(minutesAfter0((23 hours).toMinutes + 11))
    serviceClosingWarning(24, 50, dateService)(Lang("en", "en"))
      .toString should include("global.serviceCloseWarning")
  }

  "Not show warning for the 24 hours case" in {
    val dateService = mock[DateService]
    when(dateService.now).thenReturn(minutesAfter0((23 hours).toMinutes + 9))
    serviceClosingWarning(24, 50, dateService)(Lang("en", "en"))
      .toString should not include("global.serviceCloseWarning")
  }

  private def minutesAfter0(minutes: Long): Instant = {
    new org.joda.time.DateTime(0, 1, 1, 0, 0).plusMinutes(minutes.toInt).toInstant
  }
}
