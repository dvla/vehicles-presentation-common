package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.TextField
import org.scalatest.selenium.WebBrowser.textField
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.click
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.ValtechInputTextModel.Form.InputTextId

object ValtechInputTextPage extends Page {

  final val address = "/valtech-input-text"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input text"

  def documentReferenceNumberElement(implicit driver: WebDriver): TextField = textField(id(InputTextId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (documentReferenceNumber: String = "1" * 11)(implicit driver: WebDriver) {
    go to ValtechInputTextPage
    documentReferenceNumberElement.value = documentReferenceNumber
    click on submit
  }
}
