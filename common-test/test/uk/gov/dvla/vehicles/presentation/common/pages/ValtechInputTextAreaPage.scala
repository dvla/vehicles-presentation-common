package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers
import uk.gov.dvla.vehicles.presentation.common.models

import helpers.webbrowser.{Element, Page, WebBrowserDSL, TextArea, WebDriverFactory}
import models.ValtechInputTextModel.Form.InputTextId
import org.openqa.selenium.WebDriver

object ValtechInputTextAreaPage extends Page with WebBrowserDSL {

  final val address = "/valtech-input-text-area"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input text area"

  def documentReferenceNumberElement(implicit driver: WebDriver): TextArea = textArea(id(InputTextId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (documentReferenceNumber: String = "1" * 11)(implicit driver: WebDriver) {
    go to ValtechInputTextAreaPage
    documentReferenceNumberElement enter documentReferenceNumber
    click on submit
  }
}
