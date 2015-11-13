package uk.gov.dvla.vehicles.presentation.common.views


import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.ResponsiveUtilities

class ResponsiveUtilitiesSpec extends UiSpec with TestHarness {

  "A browser with Javascript Enabled" should {
    "be on a page with the correct title" in new WebBrowserWithJs {
      go to ResponsiveUtilities.url
      pageTitle should equal(ResponsiveUtilities.title)
    }

    "show elements with a class .js-only" in new WebBrowserWithJs {
      go to ResponsiveUtilities.url
      ResponsiveUtilities.assertJsOnlyVisible
    }

    "hide elements with a class .no-js-only" in new WebBrowserWithJs  {
      go to ResponsiveUtilities.url
      ResponsiveUtilities.assertNoJsOnlyInvisible
    }
  }
}
