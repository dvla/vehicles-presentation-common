package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers
import uk.gov.dvla.vehicles.presentation.common.models

import helpers.webbrowser.{Page, WebDriverFactory}
import models.MileageModel.Form.MileageId
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.textField
import org.scalatest.selenium.WebBrowser.TextField

object MileagePage extends Page {

  final val address = "/mileage"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Mileage"

  def mileageElement(implicit driver: WebDriver): TextField = textField(id(MileageId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (mileage: String = "1234")(implicit driver: WebDriver) {
    go to MileagePage
    mileageElement.value = mileage
    click on submit
  }
}
