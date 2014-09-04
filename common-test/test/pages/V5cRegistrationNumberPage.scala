package pages

import helpers.webbrowser.{Element, Page, TextField, WebBrowserDSL, WebDriverFactory}
import models.V5cRegistrationNumberModel.Form.v5cRegistrationNumberID
import org.openqa.selenium.WebDriver

object V5cRegistrationNumberPage extends Page with WebBrowserDSL {
  final val address = "/v5c-registration-number"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "V5c Registration Number"

  def registrationNumber(implicit driver: WebDriver): TextField = textField(id(v5cRegistrationNumberID))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(v5cRegistrationNumber: String = "A1")(implicit driver: WebDriver) = {
    go to V5cRegistrationNumberPage
    registrationNumber enter v5cRegistrationNumber
    click on submit
  }
}
