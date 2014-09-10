package pages

import helpers.webbrowser.{Element, Page, WebBrowserDSL, TextField, WebDriverFactory}
import models.MileageModel.Form.MileageId
import org.openqa.selenium.WebDriver

object MileagePage extends Page with WebBrowserDSL {

  final val address = "/mileage"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Mileage"

  def mileageElement(implicit driver: WebDriver): TextField = textField(id(MileageId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (mileage: String = "1234")(implicit driver: WebDriver) {
    go to MileagePage
    mileageElement enter mileage
    click on submit
  }
}
