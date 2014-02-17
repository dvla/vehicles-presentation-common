package controllers.change_of_address

import org.scalatest.WordSpec
import org.scalatest.Matchers
import mappings.change_of_address.LoginPage
import LoginPage._
import org.scalatest.mock.MockitoSugar
import helpers.change_of_address.LoginPagePopulate._

class LoginPageFormSpec extends WordSpec with Matchers with MockitoSugar {
  "loginPage form" should {
    val mockWebService = mock[services.LoginWebService]
    val loginPage = new LoginPage(mockWebService)

    def loginPageForm(username: String, password: String) = {
      loginPage.loginPageForm.bind(
        Map(
          usernameId -> username,
          passwordId -> password
        )
      )
    }

    "Accept when only mandatory fields are filled in" in {
      loginPageForm(username = usernameValid, password = passwordValid).fold(
        formWithErrors => {
          fail("Should be success instead failure has occured")
        },
        f => {
          f.username should equal(usernameValid)
          f.password should equal(passwordValid)
        }
      )
    }
  }
}