package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers
import uk.gov.dvla.vehicles.presentation.common.models

import helpers.webbrowser.{Page, WebDriverFactory}
import models.ValtechInputTextModel.Form.InputTextId
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.TextArea
import org.scalatest.selenium.WebBrowser.textArea
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.click

object ValtechInputTextAreaPage extends Page {

  final val address = "/valtech-input-text-area"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input text area"

  def documentReferenceNumberElement(implicit driver: WebDriver): TextArea = textArea(id(InputTextId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (documentReferenceNumber: String = "1" * 11)(implicit driver: WebDriver) {
    go to ValtechInputTextAreaPage
    documentReferenceNumberElement.value = documentReferenceNumber
    click on submit
  }
}
