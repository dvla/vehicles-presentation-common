package views

import com.github.nscala_time.time.Imports.LocalDate
import helpers.UiSpec
import helpers.webbrowser.TestHarness
import org.joda.time.chrono.ISOChronology
import pages.{DateOfSalePage, ErrorPanel}
import play.api.i18n.Messages

class DateOfSaleIntegrationSpec extends UiSpec with TestHarness {

  "Optional date of birth field" should {
    "be on a page with the correct title" in new WebBrowser {
      go to DateOfSalePage.instance
      page.title should equal(DateOfSalePage.instance.title)
    }

    "allow no values to be input" in new WebBrowser {
      DateOfSalePage.instance.navigate("", "", "")
      page.title should equal("Success")
    }

    "validate partial input" in new WebBrowser {
      DateOfSalePage.instance.navigate("", "", "1920")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the day if there is any input" in new WebBrowser {
      DateOfSalePage.instance.navigate("oij", "04", "1950")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the moth if there is any input" in new WebBrowser {
      DateOfSalePage.instance.navigate("01", "we", "1950")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the year if there is any input" in new WebBrowser {
      DateOfSalePage.instance.navigate("01", "04", "wwer")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the whole date is not in the future" in new WebBrowser {
      val chronology = ISOChronology.getInstance()
      val now = System.currentTimeMillis()
      val day = chronology.dayOfMonth().get(now)
      val month = chronology.monthOfYear().get(now)
      val year = chronology.year().get(now)

      DateOfSalePage.instance.navigate((day + 1).toString, month.toString, year.toString)



//      ErrorPanel.text should include(Messages("error.date.inTheFuture"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Pass trough valid dates" in new WebBrowser {
      def success(day: Int, month: Int, year: Int): Unit = {
        DateOfSalePage.instance.navigate(day.toString, month.toString, year.toString)
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
      DateOfSalePage.instance.navigate("1", "1", "1939", "", "", "")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}