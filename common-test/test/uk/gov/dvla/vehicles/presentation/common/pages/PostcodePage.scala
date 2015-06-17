package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Element
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.TextField
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDSL
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.PostcodeModel.Form.PostcodeId

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
