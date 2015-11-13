package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.{click, go}
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.PleaseWaitPage


class PleaseWaitSpec extends UiSpec with TestHarness {
  "Please wait" should {
    "be shown and hidden with javascript" in new WebBrowserWithJs {
      go to PleaseWaitPage.url
      PleaseWaitPage.title should equal(PleaseWaitPage.title)
      PleaseWaitPage.waitUntilJavascriptReady

      PleaseWaitPage.toggleOverlay
      PleaseWaitPage.assetComponentVisible(PleaseWaitPage.pleaseWaitOverlayCssSelector)

      PleaseWaitPage.toggleOverlay
      PleaseWaitPage.assetComponentInvisible(PleaseWaitPage.pleaseWaitOverlayCssSelector)
    }
    "be closed when the close message linked is clicked" in new WebBrowserWithJs {
      go to PleaseWaitPage.url
      PleaseWaitPage.waitUntilJavascriptReady

      PleaseWaitPage.toggleOverlay
      PleaseWaitPage.assetComponentVisible(PleaseWaitPage.pleaseWaitCloseAnchorCssSelector)

      click on PleaseWaitPage.closeOverlay

      PleaseWaitPage.assetComponentInvisible(PleaseWaitPage.pleaseWaitOverlayCssSelector)
    }
  }
}
