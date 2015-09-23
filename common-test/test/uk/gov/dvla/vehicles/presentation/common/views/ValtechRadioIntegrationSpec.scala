package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import org.scalatest.selenium.WebBrowser.pageSource
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.ValtechRadioPage

class ValtechRadioIntegrationSpec extends UiSpec with TestHarness {

  "ValtechRadio integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to ValtechRadioPage
      pageTitle should equal(ValtechRadioPage.title)
    }

    "redirects to the next page given valid input of private keeper" in new WebBrowserForSelenium {
      ValtechRadioPage.navigate()
      pageTitle should equal("Success")
      pageSource should include("Success - you selected a keeper type of Private")
    }

    "redirects to the next page given valid input of business keeper" in new WebBrowserForSelenium {
      ValtechRadioPage.navigate(isPrivateOwner = false)
      pageTitle should equal("Success")
      pageSource should include("Success - you selected a keeper type of Business")
    }
  }
}
