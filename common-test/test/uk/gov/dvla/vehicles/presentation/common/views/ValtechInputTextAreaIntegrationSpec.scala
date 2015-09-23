package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechInputTextAreaPage}

class ValtechInputTextAreaIntegrationSpec extends UiSpec with TestHarness {

  "ValtechInputTextArea integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to ValtechInputTextAreaPage
      pageTitle should equal(ValtechInputTextAreaPage.title)
    }

    "redirects to the next page given valid input" in new WebBrowserForSelenium {
      ValtechInputTextAreaPage.navigate()
      pageTitle should equal("Success") // Check the new title of the success page
    }

    "reject submit when field is blank" in new WebBrowserForSelenium {
      ValtechInputTextAreaPage.navigate(documentReferenceNumber = "")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "reject submit when field contains less than minimum characters" in new WebBrowserForSelenium {
      ValtechInputTextAreaPage.navigate(documentReferenceNumber = "1")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when vehicleReferenceNumber contains more than maximum characters" in new WebBrowserForSelenium {
      ValtechInputTextAreaPage.navigate(documentReferenceNumber = "1" * 1201)
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
