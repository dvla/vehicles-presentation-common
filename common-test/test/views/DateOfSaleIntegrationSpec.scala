package views

import com.github.nscala_time.time.Imports.LocalDate
import helpers.UiSpec
import helpers.webbrowser.TestHarness
import org.joda.time.chrono.ISOChronology
import pages.{NonFutureDatePage, ErrorPanel}
import play.api.i18n.Messages

class DateOfSaleIntegrationSpec extends UiSpec with TestHarness {

  "Non future date" should {
    "be on a page with the correct title" in new WebBrowser {
      go to NonFutureDatePage.instance
      page.title should equal(NonFutureDatePage.instance.title)
    }

    "not allow no values to be input" in new WebBrowser {
      NonFutureDatePage.instance.navigate("", "", "")
      page.title should equal(NonFutureDatePage.instance.title)
    }

    "display appropriate error when day is missing" in new WebBrowser {
      NonFutureDatePage.instance.navigate("", "10", "1920")
      ErrorPanel.text should include(Messages("error.nonFutureDate.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display appropriate error when month is missing" in new WebBrowser {
      NonFutureDatePage.instance.navigate("10", "", "1920")
      ErrorPanel.text should include(Messages("error.nonFutureDate.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display appropriate error when year is missing" in new WebBrowser {
      NonFutureDatePage.instance.navigate("12", "10", "")
      ErrorPanel.text should include(Messages("error.nonFutureDate.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display appropriate error when day contains letters" in new WebBrowser {
      NonFutureDatePage.instance.navigate("a", "10", "1920")
      ErrorPanel.text should include(Messages("error.nonFutureDate.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display appropriate error when month contains letters" in new WebBrowser {
      NonFutureDatePage.instance.navigate("10", "a", "1920")
      ErrorPanel.text should include(Messages("error.nonFutureDate.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display appropriate error when year contains letters" in new WebBrowser {
      NonFutureDatePage.instance.navigate("12", "10", "a")
      ErrorPanel.text should include(Messages("error.nonFutureDate.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }


    "validate the whole date is not in the future" in new WebBrowser {
      val chronology = ISOChronology.getInstance()
      val now = System.currentTimeMillis()
      val day = chronology.dayOfMonth().get(now)
      val month = chronology.monthOfYear().get(now)
      val year = chronology.year().get(now)

      NonFutureDatePage.instance.navigate((day + 1).toString, month.toString, year.toString)
      ErrorPanel.text should include(Messages("error.nonFutureDate.inTheFuture"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Pass a valid date - today" in new WebBrowser {
      val today = LocalDate.today
      NonFutureDatePage.instance.navigate(today.getDayOfMonth.toString, today.getMonthOfYear.toString, today.getYear.toString)

      page.title should equal("Success")
    }

    "Pass a valid date - in the past" in new WebBrowser {
      val today = LocalDate.today
      NonFutureDatePage.instance.navigate("15", "10", "1980")

      page.title should equal("Success")
    }
  }
}