package pages

import helpers.webbrowser.{Element, SingleSel, WebDriverFactory, Page, WebBrowserDSL}
import models.ValtechSelectModel.Form.{FirstOption, SelectId}
import org.openqa.selenium.WebDriver

object ValtechSelectPage extends Page with WebBrowserDSL {

  final val address = "/valtech-select"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech select"

  def chooseAddress(implicit driver: WebDriver): SingleSel = singleSel(id(SelectId))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (listOption: String = FirstOption)(implicit driver: WebDriver) {
    go to ValtechSelectPage
    chooseAddress.value = listOption
    click on submit
  }
}
