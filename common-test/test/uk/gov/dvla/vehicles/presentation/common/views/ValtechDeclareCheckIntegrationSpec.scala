package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.{pageTitle, click}
import org.scalatest.selenium.WebBrowser.go
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, ValtechDeclareCheckPage}

class ValtechDeclareCheckIntegrationSpec extends UiSpec with TestHarness {

  "ValtechDeclareCheck integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to ValtechDeclareCheckPage
      pageTitle should equal(ValtechDeclareCheckPage.title)
    }

    "redirect to the next page when checkbox is ticked" in new WebBrowserForSelenium {
      ValtechDeclareCheckPage.happyPath()
      pageTitle should equal("Success")
    }

    "reject when checkbox is blank" in new WebBrowserForSelenium {
      ValtechDeclareCheckPage.sadPath()
      ErrorPanel.numberOfErrors should equal(1)
    }

    "have a visible help icon with Javascript enabled" in new WebBrowserWithJs() {
      go to ValtechDeclareCheckPage

      ValtechDeclareCheckPage.assetComponentVisible(".field-help")
    }

    "have a hidden help content with Javascript enabled" in new WebBrowserWithJs {
      go to ValtechDeclareCheckPage
      ValtechDeclareCheckPage.assetComponentInvisible(".field-help-content")
    }

    "show help content when the help icon is clicked" in new WebBrowserWithJs {
      go to ValtechDeclareCheckPage
      ValtechDeclareCheckPage.waitUntilJavascriptReady

      click on ValtechDeclareCheckPage.helpIcon
      ValtechDeclareCheckPage.assetComponentVisible(".field-help-content")

      click on ValtechDeclareCheckPage.helpIcon
      ValtechDeclareCheckPage.assetComponentInvisible(".field-help-content")
    }

    "have hidden help icon with Javascript disabled" in new WebBrowserWithJsDisabled {
      go to ValtechDeclareCheckPage

      ValtechDeclareCheckPage.helpIcon.attribute("class").toString.contains("js-only") should equal(true)
    }

    "have visible help content with Javascript disabled" in new WebBrowserWithJsDisabled {
      go to ValtechDeclareCheckPage

      ValtechDeclareCheckPage.helpContent.attribute("class").toString.contains("no-js-only") should equal(true)
    }

  }
}
