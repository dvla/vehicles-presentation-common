package controllers.change_of_address

import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import controllers.{Mappings, change_of_address}
import org.scalatest.{Matchers, WordSpec}
import app.ChangeOfAddress._
import models.domain.change_of_address.LoginConfirmationModel
import org.specs2.mock.Mockito
import helpers.LoginCachePopulateHelper._

class AuthenticationControllerSpec extends WordSpec with Matchers with Mockito{

  "Authentication - Controller" should {

    "present" in new WithApplication {
      // Arrange
      loginCachePopulate()
      val request = FakeRequest().withSession()

      // Act
      val result = change_of_address.Authentication.present(request)

      // Assert
      status(result) should equal(OK)
    }

    "present login page when user is not logged in" in new WithApplication {
      // Arrange
      val request = FakeRequest().withSession()

      // Act
      val result = change_of_address.Authentication.present(request)

      // Assert
      redirectLocation(result) should equal(Some("/are-you-registered"))
    }

    "redirect to next page after the button is clicked" in new WithApplication {
      // Arrange
      val request = FakeRequest().withSession()
        .withFormUrlEncodedBody(pinFormID -> "123456")

      // Act
      val result = change_of_address.Authentication.submit(request)

      // Assert
      status(result) should equal(SEE_OTHER)
      redirectLocation(result) should equal (Some("/v5c-search"))
    }
  }
}
