package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{BusinessNamePage, ErrorPanel}

class BusinessNameIntegrationSpec extends UiSpec with TestHarness {

  "BusinessName integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to BusinessNamePage
      pageTitle should equal(BusinessNamePage.title)
    }

    "redirects to the next page given valid input" in new WebBrowserForSelenium {
      BusinessNamePage.navigate()
      pageTitle should equal("Success")
    }

    "reject submit when field is blank" in new WebBrowserForSelenium {
      BusinessNamePage.navigate(businessName = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "reject submit when field contains less than minimum characters" in new WebBrowserForSelenium {
      BusinessNamePage.navigate(businessName = "A")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when business name fails regular expression because it contains @ character" in new WebBrowserForSelenium {
      BusinessNamePage.navigate(businessName = "Foo @ Bar")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}

