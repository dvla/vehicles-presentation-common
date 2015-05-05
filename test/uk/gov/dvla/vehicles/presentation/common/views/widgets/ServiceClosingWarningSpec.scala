package uk.gov.dvla.vehicles.presentation.common.views.widgets

import org.joda.time.{DateTimeZone, Instant}
import org.mockito.Mockito.when
import play.api.i18n.Lang
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.services.{ServiceClosingWarning, DateService}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.html.widgets.serviceClosingWarning

/**
 * This passes locally but fails on jenkins. we need to find out why.
 */
class ServiceClosingWarningSpec extends UnitSpec {
  "Show x minutes before closing" in {
    val result = ServiceClosingWarning.warning(1,51, minutesAfter0(10)) // 01:00
    result should be (Some("00:50.00"))

  }

  "Not show after closing time" in {
    val result = ServiceClosingWarning.warning(1,59, minutesAfter0(61))
    result should be (None)
  }

  "Not show earlier then x minutes before closing" in {
    val result = ServiceClosingWarning.warning(1,49, minutesAfter0(10))
    result should be (None)
  }

  "Show warning for the 24 hours case" in {
    val result = ServiceClosingWarning.warning(24,50, minutesAfter0((23 hours).toMinutes + 11))
    result should be (Some("00:49.00"))
  }

  "Not show warning for the 24 hours case" in {
    val result = ServiceClosingWarning.warning(24,50, minutesAfter0((23 hours).toMinutes + 9))
    result should be (None)
  }

  private def minutesAfter0(minutes: Long) = {
    new org.joda.time.DateTime(0, 1, 1, 0, 0, DateTimeZone.forID("Europe/London")).plusMinutes(minutes.toInt)
  }
}
