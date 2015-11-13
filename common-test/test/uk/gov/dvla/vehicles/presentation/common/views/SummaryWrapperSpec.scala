package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.AppendedClues
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle}
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.SummaryWrapperPage


class SummaryWrapperSpec extends UiSpec with TestHarness with AppendedClues {
  "Summary-Wrapper widget" should {
    "have hidden content on page load with Javascript" in new WebBrowserWithJs {
      go to SummaryWrapperPage
      pageTitle should equal(SummaryWrapperPage.title)

      SummaryWrapperPage.assetComponentInvisible(SummaryWrapperPage.contentCssSelector)
    }

    "content should be visible with Javascript disabled" in new WebBrowserWithJs {
      go to SummaryWrapperPage

      SummaryWrapperPage.showHideTriggerElement.underlying.getAttribute("class").contains("no-js-only")
    }

    "have an element which can show hide content " in new WebBrowserWithJs {
      go to SummaryWrapperPage

      SummaryWrapperPage.assetComponentVisible(SummaryWrapperPage.showHideTriggerCssSelector)
    }

    "show content when show/hide is clicked, then hide it again on a second click" in new WebBrowserWithJs  {
      go to SummaryWrapperPage

      SummaryWrapperPage.waitUntilJavascriptReady

      // First click to show the content
      click on SummaryWrapperPage.showHideTriggerElement.underlying
      SummaryWrapperPage.assetComponentVisible(SummaryWrapperPage.contentCssSelector)

      // Second click, should hide the content
      click on SummaryWrapperPage.showHideTriggerElement.underlying
      SummaryWrapperPage.assetComponentInvisible(SummaryWrapperPage.contentCssSelector)
    }
  }
}
