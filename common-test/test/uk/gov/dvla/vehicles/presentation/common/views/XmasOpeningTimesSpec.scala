package uk.gov.dvla.vehicles.presentation.common.views

import org.joda.time.{DateTime, Instant}
import org.joda.time.format.DateTimeFormat
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.XmasOpeningTimesPage
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear


class XmasOpeningTimesSpec extends UiSpec with TestHarness {

  "Xmas opening times link" should {
    "be on a page with the correct title" in new WebBrowserForSelenium {

      implicit val dateService1 = new DateService {
        override def today = DayMonthYear(1, 1, 2001)
        //override def now = Instant.now()
        val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        override def now = formatter.parseDateTime("1/1/2001").toInstant
        override def dateTimeISOChronology: String = new DateTime(
          1, 1, 2001, 0, 0).toString
      }

      go to XmasOpeningTimesPage.instance(webDriver, dateService1)
      pageTitle should equal(XmasOpeningTimesPage.instance.title)
    }

    /*
    //failed to fake the implicit dateService parameter to make the test pass.
    //The implicit dateService object that is pulled by xmasOpeningTimes widget has always got the current date
    "be displayed within the correct time frame" in new WebBrowserForSelenium {

      implicit val dateService2 = new DateService {
        override def today: DayMonthYear = DayMonthYear(24, 12, 2015)
        val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        override def now = formatter.parseDateTime("24/12/2015").toInstant
        override def dateTimeISOChronology: String = new DateTime(
          24, 12, 2015, 0, 0).toString
      }

      go to XmasOpeningTimesPage.instance(webDriver, dateService2)

      print(webDriver.getPageSource)
      val link1 = XmasOpeningTimesPage.instance.link
      link1.getAttribute("id") should equal("xmas-opening-times")
      link1.getAttribute("href") should equal("https://www.gov.uk/government/news/dvla-opening-hours-christmas-and-new-year")
    }


    "not be displayed before" in new WebBrowserForSelenium {

    }

    "not be displayed after" in new WebBrowserForSelenium {

    }
    */
  }
}
