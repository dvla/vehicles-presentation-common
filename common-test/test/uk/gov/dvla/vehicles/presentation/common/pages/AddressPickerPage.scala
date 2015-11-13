package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{WebDriver}
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.id
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}
import uk.gov.dvla.vehicles.presentation.common.models.AddressPickerModel
import uk.gov.dvla.vehicles.presentation.common.views.widgetdriver.AddressPickerDriver

object AddressPickerPage  extends Page {
  final val address = "/address-picker"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Address Picker"
  val jsTestUrl = WebDriverFactory.testUrl + "jstest" + address

  val addressPickerDriver = new AddressPickerDriver(AddressPickerModel.Form.datePicker1Id)

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

}
