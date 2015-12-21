package uk.gov.dvla.vehicles.presentation.common.views

import com.google.inject.{Guice, Injector}
import com.tzavellas.sse.guice.ScalaModule
import org.joda.time.Instant
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.{TestComposition, GlobalLike, TestHarness}
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.XmasOpeningTimesPage
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

class XmasOpeningTimesSpec extends UiSpec with TestHarness {

  private def dateNow = Instant.now()
  private val xmasOpeningTimesAnchorId = "xmas-opening-times"
  private val xmasOpeningTimesLink = "https://www.gov.uk/government/news/dvla-opening-hours-christmas-and-new-year"

  val newYear = new DateService {
    override def now = dateNow
    private def date = now.toDateTime
    override def today = DayMonthYear(1, 1, date.getDayOfYear)
    override def dateTimeISOChronology: String = date.toString
  }

  val beforeDisplayDate = new DateService {
    override def now = dateNow
    private def date = now.toDateTime
    override def today = DayMonthYear(6, 12, date.getDayOfYear)
    override def dateTimeISOChronology: String = date.toString
  }

  val afterDisplayDate = new DateService {
    override def now = dateNow
    private def date = now.toDateTime
    override def today = DayMonthYear(3, 1, date.getDayOfYear)
    override def dateTimeISOChronology: String = date.toString
  }

  "Xmas opening times link" should {
    "be on a page with the correct title" in
      new WebBrowserForSelenium(LightFakeApplication(new TestGlobalWithMockDate(newYear))) {
        go to XmasOpeningTimesPage.instance
        pageTitle should equal(XmasOpeningTimesPage.instance.title)
      }

    "be displayed within the correct time frame" in
      new WebBrowserForSelenium(LightFakeApplication(new TestGlobalWithMockDate(newYear))) {
        go to XmasOpeningTimesPage.instance

        val link1 = XmasOpeningTimesPage.instance.link
        link1.getAttribute("id") should equal(xmasOpeningTimesAnchorId)
        link1.getAttribute("href") should equal(xmasOpeningTimesLink)
    }

    "not be displayed before" in
      new WebBrowserForSelenium(LightFakeApplication(new TestGlobalWithMockDate(beforeDisplayDate))) {
        go to XmasOpeningTimesPage.instance

        val link1 = XmasOpeningTimesPage.instance.xmasOpeningTimesLink
        link1.size() should equal(0)
      }

    "not be displayed after" in
      new WebBrowserForSelenium(LightFakeApplication(new TestGlobalWithMockDate(afterDisplayDate))) {
        go to XmasOpeningTimesPage.instance

        val link1 = XmasOpeningTimesPage.instance.xmasOpeningTimesLink
        link1.size() should equal(0)
      }
  }

  private class TestGlobalWithMockDate(dateService: DateService) extends GlobalLike with TestComposition {

    override lazy val injector: Injector = Guice.createInjector(testModule(new ScalaModule {
      override def configure(): Unit = { bind[DateService].toInstance(dateService) }
    }))
  }
}