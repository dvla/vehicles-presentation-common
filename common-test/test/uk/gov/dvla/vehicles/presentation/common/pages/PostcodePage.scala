package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.TextField
import org.scalatest.selenium.WebBrowser.textField
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.Element
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.PostcodeModel.Form.PostcodeId

object PostcodePage extends Page {

  final val address = "/postcode"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Postcode capture"
  val postcodeValid = "SA99 1DD"

  def businessPostcode(implicit driver: WebDriver): TextField = textField(id(PostcodeId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(postcode: String = postcodeValid)(implicit driver: WebDriver) = {
    go to PostcodePage
    businessPostcode.value = postcode
    click on submit
  }
}
