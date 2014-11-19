package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers
import uk.gov.dvla.vehicles.presentation.common.models
import helpers.webbrowser.{Element, Page, WebBrowserDSL, TextField, WebDriverFactory}
import models.BusinessNameModel.Form.BusinessNameId
import org.openqa.selenium.WebDriver

object BusinessNamePage extends Page with WebBrowserDSL {

  final val address = "/business-name"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Business name"

  def businessNameElement(implicit driver: WebDriver): TextField = textField(id(BusinessNameId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (businessName: String = "Test name")(implicit driver: WebDriver) {
    go to BusinessNamePage
    businessNameElement enter businessName
    click on submit
  }
}
