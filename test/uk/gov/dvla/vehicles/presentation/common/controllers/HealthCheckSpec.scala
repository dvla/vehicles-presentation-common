package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.test.FakeRequest
import play.mvc.Http.Status.OK
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class HealthCheckSpec extends UnitSpec {

  "requests to /healthcheck" should {

    "GET request should return 200" in {
      val result = new HealthCheck().respond(FakeRequest("GET", "/healthcheck"))
      whenReady(result) (_.header.status should equal(OK))
    }
  }
}