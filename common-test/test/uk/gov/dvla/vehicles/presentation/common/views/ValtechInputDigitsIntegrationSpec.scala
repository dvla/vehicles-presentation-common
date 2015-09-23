package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.pageTitle
import org.scalatest.selenium.WebBrowser.go
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechInputDigitsPage}

class ValtechInputDigitsIntegrationSpec extends UiSpec with TestHarness {

  "ValtechInputDigits integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to ValtechInputDigitsPage
      pageTitle should equal(ValtechInputDigitsPage.title)
    }
  }

  "displays the success page when valid input is entered" in new WebBrowserForSelenium {
    ValtechInputDigitsPage.navigate()
    pageTitle should equal("Success") // Check the new title of the success page
  }

  "reject submit when field is blank" in new WebBrowserForSelenium {
    ValtechInputDigitsPage.navigate(mileage = "")
    ErrorPanel.numberOfErrors should equal(1)
  }
}
