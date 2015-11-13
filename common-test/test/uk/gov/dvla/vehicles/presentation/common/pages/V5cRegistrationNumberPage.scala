package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser._
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.V5cRegistrationNumberModel.Form.v5cRegistrationNumberID

object V5cRegistrationNumberPage extends Page {
  final val address = "/v5c-registration-number"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "V5c Registration Number"

  def registrationNumber(implicit driver: WebDriver): TextField = textField(id(v5cRegistrationNumberID))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(v5cRegistrationNumber: String = "A1")(implicit driver: WebDriver) = {
    go to V5cRegistrationNumberPage
    registrationNumber.value = v5cRegistrationNumber
    click on submit
  }

  def helpIcon()(implicit driver: WebDriver): Element = {
    // This is the element / dom node that must be clicked to show / hide content
    find(cssSelector(".field-help")) getOrElse(throw new Exception("Unable to find help icon "))
  }

  def helpContent()(implicit driver: WebDriver): Element = {
    // This is the element / dom node that must be clicked to show / hide content
    find(cssSelector(".field-help-content")) getOrElse(throw new Exception("Unable to find help content"))
  }
}
