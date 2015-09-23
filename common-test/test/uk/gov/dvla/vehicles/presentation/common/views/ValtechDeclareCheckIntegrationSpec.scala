package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.pageTitle
import org.scalatest.selenium.WebBrowser.go
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechDeclareCheckPage}

class ValtechDeclareCheckIntegrationSpec extends UiSpec with TestHarness {

  "ValtechDeclareCheck integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to ValtechDeclareCheckPage
      pageTitle should equal(ValtechDeclareCheckPage.title)
    }

    "redirect to the next page when checkbox is ticked" in new WebBrowserForSelenium {
      ValtechDeclareCheckPage.happyPath()
      pageTitle should equal("Success")
    }

    "reject when checkbox is blank" in new WebBrowserForSelenium {
      ValtechDeclareCheckPage.sadPath()
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
