package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import java.util.TimeZone
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{JsString, JsSuccess, JsNumber}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class VehicleAndKeeperDetailsDtoSpec extends UnitSpec {
  "Reads" should {
    "convert numbers to dates in the correct time zone" in {
      timeZoneFixture {
        val someLong = new DateTime("2015-04-01T00:00:00+01:00").getMillis
        VehicleAndKeeperDetailsDto.jodaDateReads.reads(JsNumber(someLong)) should equal(
          JsSuccess(new DateTime(someLong, DateTimeZone.forID("Europe/London")))
        )
      }
    }

    "convert strings to dates in the correct time zone" in {
      timeZoneFixture {
        val someDate = new DateTime("2015-04-01T00:00:00+01:00").toString
        VehicleAndKeeperDetailsDto.jodaDateReads.reads(JsString(someDate)) should equal(
          JsSuccess(new DateTime(someDate, DateTimeZone.forID("Europe/London")))
        )
      }
    }

    "Writes" should {
      "print the datetime in the correct timezone" in {
        timeZoneFixture {
          VehicleAndKeeperDetailsDto.jodaDateWrites.writes(new DateTime("2015-04-01T00:00:00+01:00")) should equal(
            JsString("2015-04-01T00:00:00.000+01:00")
          )
        }
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
