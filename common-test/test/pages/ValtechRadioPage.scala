package pages

import helpers.webbrowser.{Element, Page, RadioButton, WebBrowserDSL, WebDriverFactory}
import org.openqa.selenium.WebDriver
import views.ValtechRadioView.{KeeperType_Business, KeeperType_Private}
import models.ValtechRadioModel.Form.KeeperTypeId

object ValtechRadioPage extends Page with WebBrowserDSL {

  final val address = "/valtech-radio"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech radio buttons"

  def privateOwner(implicit driver: WebDriver): RadioButton = radioButton(id(s"${KeeperTypeId}_$KeeperType_Private"))

  def businessOwner(implicit driver: WebDriver): RadioButton = radioButton(id(s"${KeeperTypeId}_$KeeperType_Business"))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (isPrivateOwner: Boolean = true)(implicit driver: WebDriver) {
    go to ValtechRadioPage
    if (isPrivateOwner) click on privateOwner
    else  click on businessOwner
    click on submit
  }

}
