package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.pageTitle
import org.scalatest.selenium.WebBrowser.go
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechSelectPage}

class ValtechSelectIntegrationSpec extends UiSpec with TestHarness {

  "ValtechSelect integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to ValtechSelectPage
      pageTitle should equal(ValtechSelectPage.title)
    }

    "redirects to the next page given valid input" in new WebBrowserForSelenium {
      ValtechSelectPage.navigate()
      pageTitle should equal("Success")
    }

    "display validation error when nothing selected" in new WebBrowserForSelenium {
      ValtechSelectPage.navigate("")
      ErrorPanel.numberOfErrors should equal(2)
    }
  }
}
