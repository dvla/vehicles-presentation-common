package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages

import com.github.nscala_time.time.Imports.LocalDate
import pages.{DatePage, ErrorPanel}
import play.api.i18n.Messages

class DateSpec extends UiSpec with TestHarness {

  "Optional date of birth field" should {
    "be on a page with the correct title" in new WebBrowser {
      go to DatePage.instance
      page.title should equal(DatePage.instance.title)
    }

    "allow no values to be input" in new WebBrowser {
      DatePage.instance.navigate("", "", "")
      page.title should equal("Success")
    }

    "validate partial input" in new WebBrowser {
      DatePage.instance.navigate("", "", "1920")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the day if there is any input" in new WebBrowser {
      DatePage.instance.navigate("oij", "04", "1950")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the moth if there is any input" in new WebBrowser {
      DatePage.instance.navigate("01", "we", "1950")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the year if there is any input" in new WebBrowser {
      DatePage.instance.navigate("01", "04", "wwer")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Pass trough valid dates" in new WebBrowser {
      def success(day: Int, month: Int, year: Int): Unit = {
        DatePage.instance.navigate(day.toString, month.toString, year.toString)
        page.title should equal("Success")
      }
      success(1, 2, 2003)
      success(31, 12, 1934)
      val today = LocalDate.today
      success(today.getDayOfMonth, today.getMonthOfYear, today.getYear)
    }
  }

  "Required date of birth" should {
    "Not allow any empty fields" in new WebBrowser {
      DatePage.instance.navigate("1", "1", "1939", "", "", "")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
