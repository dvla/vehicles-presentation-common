package pages

import helpers.webbrowser._
import models.EmailModel.Form.EmailId
import org.openqa.selenium.WebDriver

object EmailPage extends Page with WebBrowserDSL {

  final val address = "/email"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Email capture"
  val emailValid = "test@test.com"

  def businessEmail(implicit driver: WebDriver): EmailField = emailField(id(EmailId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(email: String = emailValid)(implicit driver: WebDriver) = {
    go to EmailPage
    businessEmail enter email
    click on submit
  }
}
