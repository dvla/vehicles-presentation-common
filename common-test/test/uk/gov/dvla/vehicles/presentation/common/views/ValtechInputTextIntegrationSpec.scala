package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.pages
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import pages.{ErrorPanel, ValtechInputTextPage}

class ValtechInputTextIntegrationSpec extends UiSpec with TestHarness {

  "ValtechInputText integration" should {
    "be presented" in new WebBrowser {
      go to ValtechInputTextPage
      page.title should equal(ValtechInputTextPage.title)
    }

    "redirects to the next page given valid input" in new WebBrowser {
      ValtechInputTextPage.navigate()
      page.title should equal("Success") // Check the new title of the success page
    }

    "reject submit when field is blank" in new WebBrowser {
      ValtechInputTextPage.navigate(documentReferenceNumber = "")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "reject submit when field contains less than minimum characters" in new WebBrowser {
      ValtechInputTextPage.navigate(documentReferenceNumber = "1")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when vehicleReferenceNumber contains more than maximum characters" in new WebBrowser {
      ValtechInputTextPage.navigate(documentReferenceNumber = "1" * 12)
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
