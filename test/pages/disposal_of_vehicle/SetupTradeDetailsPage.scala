package pages.disposal_of_vehicle

import org.openqa.selenium.WebDriver
import helpers.webbrowser._
import helpers.disposal_of_vehicle.Helper._
import services.fakes.FakeAddressLookupService

object SetupTradeDetailsPage extends Page with WebBrowserDSL {
  val address = "/disposal-of-vehicle/setup-trade-details"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  override val title: String = "Provide your trader details"

  def dealerName(implicit driver: WebDriver): TextField = textField(id("dealerName"))

  def dealerPostcode(implicit driver: WebDriver): TextField = textField(id("dealerPostcode"))

  def lookup(implicit driver: WebDriver): Element = find(xpath("//button[@type='submit' and @name=\"action\"]")).get

  def happyPath(implicit driver: WebDriver, traderBusinessName: String = traderBusinessNameValid, traderPostcode: String = postcodeValid) = {
    go to SetupTradeDetailsPage
    dealerName enter traderBusinessName
    dealerPostcode enter traderPostcode
    click on lookup
  }

  def submitInvalidPostcode(implicit driver: WebDriver) = {
    go to SetupTradeDetailsPage
    dealerName enter traderBusinessNameValid
    dealerPostcode enter FakeAddressLookupService.postcodeInvalid
    click on lookup
  }
}