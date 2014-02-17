package controllers.change_of_address

import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import controllers.change_of_address
import org.scalatest.{Matchers, WordSpec}
import org.specs2.mock.Mockito
import helpers.change_of_address.LoginCachePopulate._
import helpers.change_of_address.{AreYouRegisteredPage, AuthenticationPopulate}

class LoginConfirmationControllerSpec extends WordSpec with Matchers with Mockito {

  "LoginConfirmation - Controller" should {

    "present" in new WithApplication {
      // Arrange
      val request = FakeRequest().withSession()
      setupCache

      // Act
      val result = change_of_address.LoginConfirmation.present(request)

      // Assert
      status(result) should equal(OK)
    }
  }

  "redirect to login page when user is not logged in" in new WithApplication {
    // Arrange
    val request = FakeRequest().withSession()

    // Act
    val result = change_of_address.LoginConfirmation.present(request)

    // Assert
    redirectLocation(result) should equal(Some(AreYouRegisteredPage.url))
  }

    "redirect to next page after the agree button is clicked" in new WithApplication {
      // Arrange
      val request = FakeRequest().withSession()

      // Act
      val result = change_of_address.LoginConfirmation.submit(request)

      // Assert
      status(result) should equal(SEE_OTHER)
      redirectLocation(result) should equal (Some(AuthenticationPopulate.url))
    }
}