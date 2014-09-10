package views

import helpers.UiSpec
import helpers.webbrowser.TestHarness
import org.joda.time.chrono.ISOChronology
import pages.{ErrorPanel, DateOfBirthPage}
import play.api.i18n.Messages
import com.github.nscala_time.time.Imports.LocalDate

class DateOfBirthSpec extends UiSpec with TestHarness {

  "Optional date of birth field" should {
    "be on a page with the correct title" in new WebBrowser {
      go to DateOfBirthPage.instance
      page.title should equal(DateOfBirthPage.instance.title)
    }

    "allow no values to be input" in new WebBrowser {
      DateOfBirthPage.instance.navigate("", "", "")
      page.title should equal("Success")
    }

    "validate partial input" in new WebBrowser {
      DateOfBirthPage.instance.navigate("", "", "1920")
      ErrorPanel.text should include(Messages("error.dateOfBirth.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the day if there is any input" in new WebBrowser {
      DateOfBirthPage.instance.navigate("oij", "04", "1950")
      ErrorPanel.text should include(Messages("error.dateOfBirth.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the moth if there is any input" in new WebBrowser {
      DateOfBirthPage.instance.navigate("01", "we", "1950")
      ErrorPanel.text should include(Messages("error.dateOfBirth.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the year if there is any input" in new WebBrowser {
      DateOfBirthPage.instance.navigate("01", "04", "wwer")
      ErrorPanel.text should include(Messages("error.dateOfBirth.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "validate the whole date is not in the future" in new WebBrowser {
      val chronology = ISOChronology.getInstance()
      val now = System.currentTimeMillis()
      val day = chronology.dayOfMonth().get(now)
      val month = chronology.monthOfYear().get(now)
      val year = chronology.year().get(now)

      DateOfBirthPage.instance.navigate((day + 1).toString, month.toString, year.toString)
      ErrorPanel.text should include(Messages("error.dateOfBirth.inTheFuture"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Pass trough valid dates" in new WebBrowser {
      def success(day: Int, month: Int, year: Int): Unit = {
        DateOfBirthPage.instance.navigate(day.toString, month.toString, year.toString)
        page.title should equal("Success")
      }
      success(1, 2, 3)
      success(31, 12, 1234)
      val today = LocalDate.today
      success(today.getDayOfMonth, today.getMonthOfYear, today.getYear)
    }
  }

  "Required date of birth" should {
    "Not allow any empty fields" in new WebBrowser {
      DateOfBirthPage.instance.navigate("1", "1", "1111", "", "", "")
      ErrorPanel.text should include(Messages("error.dateOfBirth.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
