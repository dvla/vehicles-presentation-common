package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, PostcodePage}

class PostcodeIntegrationSpec extends UiSpec with TestHarness {

  "Postcode integration " should {
    "display the page" in new WebBrowserForSelenium {
      go to PostcodePage
      pageTitle should equal(PostcodePage.title)
    }

    "display success message when correct data is entered" in new WebBrowserForSelenium {
      PostcodePage.navigate()
      pageTitle should equal("Success")
    }

    "display three validation error messages when no postcode is entered" in new WebBrowserForSelenium {
      PostcodePage.navigate(postcode = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "display two validation error messages when a postcode less than min length is entered" in new WebBrowserForSelenium {
      PostcodePage.navigate(postcode = "SA99")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "display one validation error message when a postcode containing a special character is entered" in new WebBrowserForSelenium {
      PostcodePage.navigate(postcode = "SA99 1D£")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a postcode containing an incorrect format" in new WebBrowserForSelenium {
      PostcodePage.navigate(postcode = "SAT999")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
