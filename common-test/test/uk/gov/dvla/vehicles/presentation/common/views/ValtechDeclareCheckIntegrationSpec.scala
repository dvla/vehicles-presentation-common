package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.pages
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import pages.{ErrorPanel, ValtechDeclareCheckPage}

class ValtechDeclareCheckIntegrationSpec extends UiSpec with TestHarness {

  "ValtechDeclareCheck integration" should {
    "be presented" in new WebBrowser {
      go to ValtechDeclareCheckPage
      page.title should equal(ValtechDeclareCheckPage.title)
    }

    "redirect to the next page when checkbox is ticked" in new WebBrowser {
      ValtechDeclareCheckPage.happyPath()
      page.title should equal("Success")
    }

    "reject when checkbox is blank" in new WebBrowser {
      ValtechDeclareCheckPage.sadPath()
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
