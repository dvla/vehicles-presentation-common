package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Element
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.TelField
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDSL
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.ValtechInputDigitsModel.Form.MileageId

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
