package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers
import uk.gov.dvla.vehicles.presentation.common.models
import helpers.webbrowser.{WebDriverFactory, Page}
import models.ValtechDeclareCheckModel.Form.DeclareSelectId
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.Checkbox
import org.scalatest.selenium.WebBrowser.checkbox
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser._

object ValtechDeclareCheckPage extends Page {

  final val address = "/valtech-declare-check"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech declare check"

  def checkboxElement(implicit driver: WebDriver): Checkbox = checkbox(id(DeclareSelectId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def happyPath()(implicit driver: WebDriver) {
    go to ValtechDeclareCheckPage
    click on checkboxElement
    click on submit
  }

  def sadPath ()(implicit driver: WebDriver) {
    go to ValtechDeclareCheckPage
    click on submit
  }

  def helpIcon()(implicit driver: WebDriver): Element = {
    // This is the element / dom node that must be clicked to show / hide content
    find(cssSelector(".field-help")) getOrElse(throw new Exception("Unable to find help icon "))
  }

  def helpContent()(implicit driver: WebDriver): Element = {
    // This is the element / dom node that must be clicked to show / hide content
    find(cssSelector(".field-help-content")) getOrElse(throw new Exception("Unable to find help content"))
  }
}
