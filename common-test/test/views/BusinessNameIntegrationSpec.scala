package views

import helpers.webbrowser.TestHarness
import helpers.UiSpec
import pages.{ErrorPanel, BusinessNamePage}

class BusinessNameIntegrationSpec extends UiSpec with TestHarness {

  "BusinessName integration" should {
    "be presented" in new WebBrowser {
      go to BusinessNamePage
      page.title should equal(BusinessNamePage.title)
    }

    "redirects to the next page given valid input" in new WebBrowser {
      BusinessNamePage.navigate()
      page.title should equal("Success")
    }

    "reject submit when field is blank" in new WebBrowser {
      BusinessNamePage.navigate(businessName = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "reject submit when field contains less than minimum characters" in new WebBrowser {
      BusinessNamePage.navigate(businessName = "A")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when business name fails regular expression because it contains ampersand" in new WebBrowser {
      BusinessNamePage.navigate(businessName = "Foo & Bar")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}

