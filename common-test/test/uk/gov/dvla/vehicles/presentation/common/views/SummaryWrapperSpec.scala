package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.AppendedClues
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.SummaryWrapperPage


class SummaryWrapperSpec extends UiSpec with TestHarness with AppendedClues {
  "Summary-Wrapper widget" should {
    "have hidden content on page load with Javascript" in new WebBrowserWithJs {
      go to SummaryWrapperPage
      pageTitle should equal(SummaryWrapperPage.title)

      !SummaryWrapperPage.contentElement.isDisplayed
    }
    "have the content shown on page load without Javascript enabled" in new WebBrowserWithJsDisabled {
      go to SummaryWrapperPage

      SummaryWrapperPage.contentElement.isDisplayed
    }
    "have an element which can show hide content " in new WebBrowserWithJs {
      go to SummaryWrapperPage

      SummaryWrapperPage.showHideTriggerElement.isDisplayed
    }
    "show content when show/hide is clicked, then hide it again on a second click" in new WebBrowserWithJs  {
      go to SummaryWrapperPage

      // First click to show the content
      click on SummaryWrapperPage.showHideTriggerElement
      SummaryWrapperPage.contentElement.isDisplayed

      // Second click, should hide the content
      click on SummaryWrapperPage.showHideTriggerElement
      !SummaryWrapperPage.contentElement.isDisplayed
    }
  }
}
