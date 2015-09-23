package uk.gov.dvla.vehicles.presentation.common.views

import com.github.nscala_time.time.Imports.LocalDate
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{DatePage, ErrorPanel}

class DateSpec extends UiSpec with TestHarness {

  "Optional date of birth field" should {
    "be on a page with the correct title" in new WebBrowserForSelenium {
      go to DatePage.instance
      pageTitle should equal(DatePage.instance.title)
    }

//    "allow no values to be input" in new WebBrowserForSelenium {
//      DatePage.instance.navigate("", "", "")
//      pageTitle should equal("Success")
//    }
//
//    "validate partial input" in new WebBrowserForSelenium {
//      DatePage.instance.navigate("", "", "1920")
//      ErrorPanel.text should include(Messages("error.date.invalid"))
//      ErrorPanel.numberOfErrors should equal(1)
//    }
//
//    "validate the day if there is any input" in new WebBrowserForSelenium {
//      DatePage.instance.navigate("oij", "04", "1950")
//      ErrorPanel.text should include(Messages("error.date.invalid"))
//      ErrorPanel.numberOfErrors should equal(1)
//    }
//
//    "validate the moth if there is any input" in new WebBrowserForSelenium {
//      DatePage.instance.navigate("01", "we", "1950")
//      ErrorPanel.text should include(Messages("error.date.invalid"))
//      ErrorPanel.numberOfErrors should equal(1)
//    }
//
//    "validate the year if there is any input" in new WebBrowserForSelenium {
//      DatePage.instance.navigate("01", "04", "wwer")
//      ErrorPanel.text should include(Messages("error.date.invalid"))
//      ErrorPanel.numberOfErrors should equal(1)
//    }
//
//    "Pass trough valid dates" in new WebBrowserForSelenium {
//      def success(day: String, month: String, year: String): Unit = {
//        DatePage.instance.navigate(day, month, year)
//        pageTitle should equal("Success")
//      }
//      success("01", "02", "2003")
//      success("31", "12", "1934")
//      val today = LocalDate.today
//      success(today.toString("dd"), today.toString("MM"), today.toString("YYYY"))
//    }
  }
//
//  "Required date of birth" should {
//    "Not allow any empty fields" in new WebBrowserForSelenium {
//      DatePage.instance.navigate("01", "01", "1939", "", "", "")
//      ErrorPanel.text should include(Messages("error.date.invalid"))
//      ErrorPanel.numberOfErrors should equal(1)
//    }
//  }
}
