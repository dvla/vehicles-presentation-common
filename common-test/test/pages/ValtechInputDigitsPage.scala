package pages

import helpers.webbrowser.{Element, TelField, WebBrowserDSL, Page, WebDriverFactory}
import models.ValtechInputDigitsModel.Form.MileageId
import org.openqa.selenium.WebDriver

object ValtechInputDigitsPage extends Page with WebBrowserDSL {

  final val address = "/valtech-input-digits"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input digits"

  def mileageElement(implicit driver: WebDriver): TelField = telField(id(MileageId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (mileage: String = "40000")(implicit driver: WebDriver) {
    go to ValtechInputDigitsPage
    mileageElement enter mileage
    click on submit
  }
}
