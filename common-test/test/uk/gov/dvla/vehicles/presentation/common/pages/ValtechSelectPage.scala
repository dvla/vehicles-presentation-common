package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Element
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.SingleSel
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDSL
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.ValtechSelectModel.Form.{FirstOption, SelectId}

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
