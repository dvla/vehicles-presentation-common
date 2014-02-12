package views.disposal_of_vehicle

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.BrowserMatchers
import mappings.disposal_of_vehicle.Dispose._
import helpers.disposal_of_vehicle.{DisposePopulate, SetUpTradeDetailsPopulate}

class DisposeIntegrationSpec extends Specification with Tags {
  "Dispose Integration" should {
    "be presented" in new WithBrowser with BrowserMatchers {
      // Arrange & Act
      SetUpTradeDetailsPopulate.happyPath(browser)
      browser.goTo(DisposePopulate.url)

      // Check the page title is correct
      titleMustEqual("Dispose a vehicle into the motor trade: confirm")
    }

    "display the next page when mandatory data is entered and dispose button is clicked" in new WithBrowser with BrowserMatchers {
      // Fill in mandatory data
      SetUpTradeDetailsPopulate.happyPath(browser)
      DisposePopulate.happyPath(browser)

      // Verify we have moved to the next screen
      titleMustEqual("Dispose a vehicle into the motor trade: summary")
    }

    "redirect when no traderBusinessName is cached" in new WithBrowser with BrowserMatchers {
      // Arrange & Act
      browser.goTo(DisposePopulate.url)

      // Assert
      titleMustEqual("Dispose a vehicle into the motor trade: set-up")
    }
  }
}