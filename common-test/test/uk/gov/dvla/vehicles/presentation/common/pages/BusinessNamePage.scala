package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.TextField
import org.scalatest.selenium.WebBrowser.textField
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.click
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.BusinessNameModel.Form.BusinessNameId

object BusinessNamePage extends Page {

  final val address = "/business-name"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Business name"

  def businessNameElement(implicit driver: WebDriver): TextField = textField(id(BusinessNameId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (businessName: String = "Test name")(implicit driver: WebDriver) {
    go to BusinessNamePage
    businessNameElement.value = businessName
    click on submit
  }
}
