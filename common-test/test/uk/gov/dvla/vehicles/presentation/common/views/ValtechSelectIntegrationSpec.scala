package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.TestHarness
import pages.{ErrorPanel, ValtechSelectPage}

class ValtechSelectIntegrationSpec extends UiSpec with TestHarness {

  "ValtechSelect integration" should {
    "be presented" in new WebBrowser {
      go to ValtechSelectPage
      page.title should equal(ValtechSelectPage.title)
    }

    "redirects to the next page given valid input" in new WebBrowser {
      ValtechSelectPage.navigate()
      page.title should equal("Success")
    }

    "display validation error when nothing selected" in new WebBrowser {
      ValtechSelectPage.navigate("")
      ErrorPanel.numberOfErrors should equal(2)
    }
  }
}
