package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.SearchContext
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Element, WebBrowserDSL, Page, WebDriverFactory}
import uk.gov.dvla.vehicles.presentation.common.models.AddressPickerModel
import uk.gov.dvla.vehicles.presentation.common.views.widgetdriver.AddressPickerDriver

object AddressPickerPage  extends Page with WebBrowserDSL {
  final val address = "/address-picker"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Address Picker"
  val jsTestUrl = WebDriverFactory.testUrl + "jstest" + address

  val addressPickerDriver = new AddressPickerDriver(AddressPickerModel.Form.datePicker1Id)

  def submit(implicit driver: SearchContext): Element = find(id("submit")).get
}
