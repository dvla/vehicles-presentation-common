package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.pages
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import pages.{ErrorPanel, ValtechInputDayMonthYearPage}

class ValtechInputDayMonthYearIntegrationSpec extends UiSpec with TestHarness {

  "Input day month year integration" should {
    "present" in new WebBrowser {
      go to ValtechInputDayMonthYearPage
      page.title should equal(ValtechInputDayMonthYearPage.title)
    }

    "accept a valid date" in new WebBrowser {
      ValtechInputDayMonthYearPage.navigate()
      page.title should equal("Success")
    }

    "display validation error message when no fields filled in" in new WebBrowser {
      ValtechInputDayMonthYearPage.navigate(day = "", month = "", year = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "display validation error when day is blank" in new WebBrowser {
      ValtechInputDayMonthYearPage.navigate(day = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display validation error when month is blank" in new WebBrowser {
      ValtechInputDayMonthYearPage.navigate(month = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display validation error when year is blank" in new WebBrowser {
      ValtechInputDayMonthYearPage.navigate(year = "")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
