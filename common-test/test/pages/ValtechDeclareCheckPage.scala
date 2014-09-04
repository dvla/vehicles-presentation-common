package pages

import helpers.webbrowser.{Checkbox, Element, WebDriverFactory, WebBrowserDSL, Page}
import models.ValtechDeclareCheckModel.Form.DeclareSelectId
import org.openqa.selenium.WebDriver

object ValtechDeclareCheckPage extends Page with WebBrowserDSL {

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
}
