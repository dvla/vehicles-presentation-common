package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechInputDayMonthYearPage}
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec

class ValtechInputDayMonthYearIntegrationSpec extends UiSpec with TestHarness {

  "Input day month year integration" should {
    "present" in new WebBrowserForSelenium {
      go to ValtechInputDayMonthYearPage
      pageTitle should equal(ValtechInputDayMonthYearPage.title)
    }

    "accept a valid date" in new WebBrowserForSelenium {
      ValtechInputDayMonthYearPage.navigate()
      pageTitle should equal("Success")
    }

    "display validation error message when no fields filled in" in new WebBrowserForSelenium {
      ValtechInputDayMonthYearPage.navigate(day = "", month = "", year = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "display validation error when day is blank" in new WebBrowserForSelenium {
      ValtechInputDayMonthYearPage.navigate(day = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display validation error when month is blank" in new WebBrowserForSelenium {
      ValtechInputDayMonthYearPage.navigate(month = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display validation error when year is blank" in new WebBrowserForSelenium {
      ValtechInputDayMonthYearPage.navigate(year = "")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
