package pages

import helpers.webbrowser.{Element, Page, WebBrowserDSL, TextField, WebDriverFactory}
import models.ValtechInputTextModel.Form.DocumentReferenceNumberId
import org.openqa.selenium.WebDriver

object ValtechInputTextPage extends Page with WebBrowserDSL {

  final val address = "/valtech-input-text"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input text"

  def documentReferenceNumberElement(implicit driver: WebDriver): TextField = textField(id(DocumentReferenceNumberId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (documentReferenceNumber: String = "1" * 11)(implicit driver: WebDriver) {
    go to ValtechInputTextPage
    documentReferenceNumberElement enter documentReferenceNumber
    click on submit
  }
}
