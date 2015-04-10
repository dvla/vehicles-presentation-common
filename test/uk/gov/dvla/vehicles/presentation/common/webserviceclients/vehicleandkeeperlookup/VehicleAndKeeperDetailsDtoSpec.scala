package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import java.util.TimeZone

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{JsString, JsSuccess, JsNumber}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class VehicleAndKeeperDetailsDtoSpec extends UnitSpec {
  "Reads" should {
    "convert numbers to dates in the correct time zone" in {
      timeZoneFixture {
        val someLong = DateTime.now().getMillis
        VehicleAndKeeperDetailsDto.jodaDateReads.reads(JsNumber(someLong)) should equal(
          JsSuccess(new DateTime(someLong, DateTimeZone.forID("Europe/London")))
        )
      }
    }

    "convert strings to dates in the correct time zone" in {
      timeZoneFixture {
        val someDate = DateTime.now().toString
        VehicleAndKeeperDetailsDto.jodaDateReads.reads(JsString(someDate)) should equal(
          JsSuccess(new DateTime(someDate, DateTimeZone.forID("Europe/London")))
        )
      }
    }
  }

  private def timeZoneFixture(test: => Unit): Unit = {
    val defaultJodaTimeZone = DateTimeZone.getDefault
    val defaultTimeZone = TimeZone.getDefault
    try {
      DateTimeZone.setDefault(DateTimeZone.forID("UTC"))
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
      test
    } finally {
      DateTimeZone.setDefault(defaultJodaTimeZone)
      TimeZone.setDefault(defaultTimeZone)
    }
  }
}
