package pages

import helpers.webbrowser.{Element, Page, TextField, WebBrowserDSL, WebDriverFactory}
import models.PostcodeModel.Form.PostcodeId
import org.openqa.selenium.WebDriver

object PostcodePage extends Page with WebBrowserDSL {

  final val address = "/postcode"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Postcode capture"
  val postcodeValid = "SA99 1DD"

  def businessPostcode(implicit driver: WebDriver): TextField = textField(id(PostcodeId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(postcode: String = postcodeValid)(implicit driver: WebDriver) = {
    go to PostcodePage
    businessPostcode enter postcode
    click on submit
  }
}
