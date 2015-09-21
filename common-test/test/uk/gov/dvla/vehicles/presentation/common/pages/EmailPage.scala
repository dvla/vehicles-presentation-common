package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser._
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import uk.gov.dvla.vehicles.presentation.common.models.EmailModel.Form.EmailId

object EmailPage extends Page with WebBrowserDSL {

  final val address = "/email"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Email capture"
  val emailValid = "test@test.com"

  def businessEmail(implicit driver: WebDriver): EmailField = emailField(id(s"${EmailId}_$EmailEnterId"))

  def businessEmailVerify(implicit driver: WebDriver): EmailField = emailField(id(s"${EmailId}_$EmailVerifyId"))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(email: String = emailValid)(implicit driver: WebDriver) = {
    go to EmailPage
    businessEmail enter email
    businessEmailVerify enter email
    click on submit
  }
}
