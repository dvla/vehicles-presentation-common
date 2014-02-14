package views.disposal_of_vehicle

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.BrowserMatchers
import helpers.disposal_of_vehicle._

class DisposeConfirmationIntegrationSpec extends Specification with Tags {
  "Dispose confirmation integration" should {
    "be presented" in new WithBrowser with BrowserMatchers {
      // Arrange & Act
      SetUpTradeDetailsPage.setupCache
      BusinessChooseYourAddressPage.setupCache
      VehicleLookupPage.setupCache
      DisposePage.setupCache
      browser.goTo(DisposeConfirmationPage.url)

      // Check the page title is correct
      titleMustEqual("Dispose a vehicle into the motor trade: summary")
    }

  "Redirect when no traderBusinessName is cached" in new WithBrowser with BrowserMatchers {
    // Arrange & Act
    browser.goTo(DisposeConfirmationPage.url)

    // Assert
    titleMustEqual("Dispose a vehicle into the motor trade: set-up")
  }

  "display previous page when back link is clicked" in new WithBrowser with BrowserMatchers {
    SetUpTradeDetailsPage.setupCache
    BusinessChooseYourAddressPage.setupCache
    VehicleLookupPage.setupCache
    DisposePage.setupCache
    browser.goTo(DisposeConfirmationPage.url)

    browser.click("#backButton")
    titleMustEqual("Dispose a vehicle into the motor trade: confirm")
  }

    "display vehicle lookup page when new disposal link is clicked" in new WithBrowser with BrowserMatchers {
      SetUpTradeDetailsPage.setupCache
      BusinessChooseYourAddressPage.setupCache
      VehicleLookupPage.setupCache
      DisposePage.setupCache
      browser.goTo(DisposeConfirmationPage.url)

      browser.click("#newDisposal")
      titleMustEqual("Dispose a vehicle into the motor trade: vehicle")
    }
  }
}
