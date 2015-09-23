package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.TelField
import org.scalatest.selenium.WebBrowser.telField
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.click
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.ValtechInputDigitsModel.Form.MileageId

object ValtechInputDigitsPage extends Page {

  final val address = "/valtech-input-digits"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input digits"

  def mileageElement(implicit driver: WebDriver): TelField = telField(id(MileageId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (mileage: String = "40000")(implicit driver: WebDriver) {
    go to ValtechInputDigitsPage
    mileageElement.value = mileage
    click on submit
  }
}
