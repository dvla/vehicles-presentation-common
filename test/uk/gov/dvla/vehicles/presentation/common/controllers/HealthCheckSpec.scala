package uk.gov.dvla.vehicles.presentation.common.controllers

import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import play.mvc.Http.Status.{OK, INTERNAL_SERVER_ERROR}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.{NotHealthyStats, HealthStats}

class HealthCheckSpec extends UnitSpec {

  "requests to /healthcheck" should {

    "GET request should return 200 if the health stats are good" in {
      val healthStats = mock[HealthStats]
      when(healthStats.healthy).thenReturn(None)
      val result = new HealthCheck(healthStats).respond(FakeRequest("GET", "/healthcheck"))
      whenReady(result) (_.header.status should equal(OK))
    }

    "GET request should return 500 if the health stats are not good" in {
      val healthStats = mock[HealthStats]
      when(healthStats.healthy).thenReturn(Some(NotHealthyStats("ms1", "stats info")))
      val result = new HealthCheck(healthStats).respond(FakeRequest("GET", "/healthcheck"))
      whenReady(result) {result=>
        result.header.status should equal(INTERNAL_SERVER_ERROR)
      }
      contentAsString(result) should equal("stats info")
    }
  }
}
