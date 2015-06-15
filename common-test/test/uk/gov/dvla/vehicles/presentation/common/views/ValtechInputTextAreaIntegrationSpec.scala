package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechInputTextAreaPage}

class ValtechInputTextAreaIntegrationSpec extends UiSpec with TestHarness {

  "ValtechInputTextArea integration" should {
    "be presented" in new WebBrowser {
      go to ValtechInputTextAreaPage
      page.title should equal(ValtechInputTextAreaPage.title)
    }

    "redirects to the next page given valid input" in new WebBrowser {
      ValtechInputTextAreaPage.navigate()
      page.title should equal("Success") // Check the new title of the success page
    }

    "reject submit when field is blank" in new WebBrowser {
      ValtechInputTextAreaPage.navigate(documentReferenceNumber = "")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "reject submit when field contains less than minimum characters" in new WebBrowser {
      ValtechInputTextAreaPage.navigate(documentReferenceNumber = "1")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when vehicleReferenceNumber contains more than maximum characters" in new WebBrowser {
      ValtechInputTextAreaPage.navigate(documentReferenceNumber = "1" * 1201)
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
