package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, V5cRegistrationNumberPage}

class V5cRegistrationNumberIntegrationSpec extends UiSpec with TestHarness {

  "V5cRegistration number integration" should {
    "display the page" in new WebBrowserForSelenium {
      go to V5cRegistrationNumberPage
      pageTitle should equal(V5cRegistrationNumberPage.title)
    }

    "displays success page when correct data is entered" in new WebBrowserForSelenium {
      V5cRegistrationNumberPage.navigate()
      pageTitle should equal("Success") // Check the new title of the success page
    }

    "reject submit when field is blank" in new WebBrowserForSelenium {
      V5cRegistrationNumberPage.navigate(v5cRegistrationNumber = "")
      ErrorPanel.numberOfErrors should equal(3)
    }
  }
}
