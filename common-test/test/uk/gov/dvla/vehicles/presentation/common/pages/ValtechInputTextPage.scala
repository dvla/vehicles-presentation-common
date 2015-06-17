package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Element
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.TextField
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDSL
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.ValtechInputTextModel.Form.InputTextId

object ValtechInputTextPage extends Page with WebBrowserDSL {

  final val address = "/valtech-input-text"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input text"

  def documentReferenceNumberElement(implicit driver: WebDriver): TextField = textField(id(InputTextId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (documentReferenceNumber: String = "1" * 11)(implicit driver: WebDriver) {
    go to ValtechInputTextPage
    documentReferenceNumberElement enter documentReferenceNumber
    click on submit
  }
}
