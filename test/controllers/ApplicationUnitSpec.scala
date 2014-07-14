package controllers

import helpers.{UnitSpec, WithApplication}
import org.mockito.Mockito.when
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import utils.helpers.Config

final class ApplicationUnitSpec extends UnitSpec {
  "index" should {

    "redirect the user to the start url" in new WithApplication {
      implicit val config = configWithStartUrl("/testStart")
      val result = new ApplicationRoot().index(FakeRequest())
      redirectLocation(result) should equal(Some("/testStart"))
    }

    "redirect the user to the start url when the start url does not have a starting slash" in new WithApplication {
      implicit val config = configWithStartUrl("testStart")
      val result = new ApplicationRoot().index(FakeRequest())
      redirectLocation(result) should equal(Some("/testStart"))
    }

    "redirect the user to the start url when there is an application context set " in
      new WithApplication(FakeApplication(additionalConfiguration = Map("application.context" -> "/testContext/"))) {
        implicit val config = configWithStartUrl("/testStart")
        val result = new ApplicationRoot().index(FakeRequest())
        redirectLocation(result) should equal(Some("/testContext/testStart"))
      }
  }

  private def configWithStartUrl(startUrl: String): Config = {
    val config = mock[Config]
    when(config.startUrl).thenReturn(startUrl)
    config
  }
}