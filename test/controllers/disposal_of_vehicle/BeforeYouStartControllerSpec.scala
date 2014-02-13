package controllers.disposal_of_vehicle

import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import controllers.disposal_of_vehicle
import org.scalatest.{Matchers, WordSpec}
import helpers.disposal_of_vehicle.SetUpTradeDetailsPage

class BeforeYouStartControllerSpec extends WordSpec with Matchers {

  "BeforeYouStart - Controller" should {

    "present" in new WithApplication {
      // Arrange
      val request = FakeRequest().withSession()

      // Act
      val result = disposal_of_vehicle.BeforeYouStart.present(request)

      // Assert
      status(result) should equal(OK)
    }

    "redirect to next page after the button is clicked" in new WithApplication {
      // Arrange
      val request = FakeRequest().withSession()

      // Act
      val result = disposal_of_vehicle.BeforeYouStart.submit(request)

      // Assert
      status(result) should equal(SEE_OTHER)
      redirectLocation(result) should equal (Some(SetUpTradeDetailsPage.url))
     }
  }
}