package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.{click, go}
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{V5cRegistrationNumberPage, ErrorPanel}

class V5cRegistrationNumberIntegrationSpec extends UiSpec with TestHarness {

  "V5cRegistration number integration" should {
    "display the page" in new WebBrowserForSelenium {
      go to V5cRegistrationNumberPage
      pageTitle should equal(V5cRegistrationNumberPage.title)
    }

    "displays success page when correct data is entered" in new WebBrowserForSelenium {
      V5cRegistrationNumberPage.navigate()
      pageTitle should equal("Success") // Check the new title of the success page
    }

    "reject submit when field is blank" in new WebBrowserForSelenium {
      V5cRegistrationNumberPage.navigate(v5cRegistrationNumber = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "have a visible help icon with Javascript enabled" in new WebBrowserWithJs {
      go to V5cRegistrationNumberPage

      V5cRegistrationNumberPage.assetComponentVisible(".field-help")
    }

    "have a hidden help content with Javascript enabled" in new WebBrowserWithJs {
      go to V5cRegistrationNumberPage
      V5cRegistrationNumberPage.assetComponentInvisible(".field-help-content")
    }

    "show help content when the help icon is clicked" in new WebBrowserWithJs {
      go to V5cRegistrationNumberPage
      V5cRegistrationNumberPage.waitUntilJavascriptReady

      click on V5cRegistrationNumberPage.helpIcon
      V5cRegistrationNumberPage.assetComponentVisible(".field-help-content")

      click on V5cRegistrationNumberPage.helpIcon
      V5cRegistrationNumberPage.assetComponentInvisible(".field-help-content")
    }


    "have hidden help icon with Javascript disabled" in new WebBrowserWithJsDisabled {
      go to V5cRegistrationNumberPage

      V5cRegistrationNumberPage.helpIcon.attribute("class").toString.contains("js-only") should equal(true)
    }

    "have visible help content with Javascript disabled" in new WebBrowserWithJsDisabled {
      go to V5cRegistrationNumberPage

      V5cRegistrationNumberPage.helpContent.attribute("class").toString.contains("no-js-only") should equal(true)
    }
  }
}
