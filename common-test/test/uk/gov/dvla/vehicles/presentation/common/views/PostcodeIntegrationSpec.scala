package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, PostcodePage}

class PostcodeIntegrationSpec extends UiSpec with TestHarness {

  "Postcode integration " should {
    "display the page" in new WebBrowser {
      go to PostcodePage
      page.title should equal(PostcodePage.title)
    }

    "display success message when correct data is entered" in new WebBrowser {
      PostcodePage.navigate()
      page.title should equal("Success")
    }

    "display three validation error messages when no postcode is entered" in new WebBrowser {
      PostcodePage.navigate(postcode = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "display two validation error messages when a postcode less than min length is entered" in new WebBrowser {
      PostcodePage.navigate(postcode = "SA99")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "display one validation error message when a postcode containing a special character is entered" in new WebBrowser {
      PostcodePage.navigate(postcode = "SA99 1D£")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a postcode containing an incorrect format" in new WebBrowser {
      PostcodePage.navigate(postcode = "SAT999")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
