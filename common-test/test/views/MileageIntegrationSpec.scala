package views

import helpers.webbrowser.TestHarness
import helpers.UiSpec
import pages.{ErrorPanel, MileagePage}

class MileageIntegrationSpec extends UiSpec with TestHarness {

  "Mileage integration" should {
    "be presented" in new WebBrowser {
      go to MileagePage
      page.title should equal(MileagePage.title)
    }

    "redirects to the next page given valid input" in new WebBrowser {
      MileagePage.navigate()
      page.title should equal("Success")
    }

    "reject submit when mileage is negative" in new WebBrowser {
      MileagePage.navigate(mileage = "-123")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when mileage contains letter" in new WebBrowser {
      MileagePage.navigate(mileage = "A")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}

