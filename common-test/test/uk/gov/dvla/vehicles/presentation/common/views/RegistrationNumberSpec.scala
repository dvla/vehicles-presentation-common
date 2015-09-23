package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.RegistrationNumberPage

class RegistrationNumberSpec extends UiSpec with TestHarness {

  "Registration Number" should {
    "be on a page with the correct title" in new WebBrowserForSelenium {
      go to RegistrationNumberPage.instance
      pageTitle should equal(RegistrationNumberPage.instance.title)
    }

    "be on a page with the correct registration number" in new WebBrowserForSelenium {
      go to RegistrationNumberPage.instance

      val regNumber1 = RegistrationNumberPage.instance.regNumber1
      regNumber1.getAttribute("class") should equal("reg-number")
      regNumber1.getText() should equal("A1")
    }
  }
}