package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechInputDigitsPage}

class ValtechInputDigitsIntegrationSpec extends UiSpec with TestHarness {

  "ValtechInputDigits integration" should {
    "be presented" in new WebBrowser {
      go to ValtechInputDigitsPage
      page.title should equal(ValtechInputDigitsPage.title)
    }
  }

  "displays the success page when valid input is entered" in new WebBrowser {
    ValtechInputDigitsPage.navigate()
    page.title should equal("Success") // Check the new title of the success page
  }

  "reject submit when field is blank" in new WebBrowser {
    ValtechInputDigitsPage.navigate(mileage = "")
    ErrorPanel.numberOfErrors should equal(1)
  }
}
