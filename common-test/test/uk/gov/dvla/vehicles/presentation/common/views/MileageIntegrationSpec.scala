package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, MileagePage}

class MileageIntegrationSpec extends UiSpec with TestHarness {

  "Mileage integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to MileagePage
      pageTitle should equal(MileagePage.title)
    }

    "redirects to the next page given valid input" in new WebBrowserForSelenium {
      MileagePage.navigate()
      pageTitle should equal("Success")
    }

    "reject submit when mileage is negative" in new WebBrowserForSelenium {
      MileagePage.navigate(mileage = "-123")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when mileage contains letter" in new WebBrowserForSelenium {
      MileagePage.navigate(mileage = "A")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}

