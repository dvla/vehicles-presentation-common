package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.AlertWarningPage

class AlertWarningSpec extends UiSpec with TestHarness {

  "Alert Warning" should {
    "be on a page with the correct title" in new WebBrowserForSelenium {
      go to AlertWarningPage.instance
      pageTitle should equal(AlertWarningPage.instance.title)
    }

    "be on a page with the correct warning message 1" in new WebBrowserForSelenium {
      go to AlertWarningPage.instance

      val warning1 = AlertWarningPage.instance.warning1
      warning1.getAttribute("class") should equal("alert-warning")
      warning1.getText() should equal("This is a warning")
    }

    "be on a page with the correct warning message 2" in new WebBrowserForSelenium {
      go to AlertWarningPage.instance

      val warning2 = AlertWarningPage.instance.warning2
      warning2.getAttribute("class") should equal("alert-warning print-message")
      warning2.getText() should equal("This is a second warning")
    }
  }
}
