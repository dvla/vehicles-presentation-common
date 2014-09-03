package views

import helpers.UiSpec
import helpers.webbrowser.TestHarness
import pages.ValtechRadioPage

class ValtechRadioIntegrationSpec extends UiSpec with TestHarness {

  "ValtechRadio integration" should {
    "be presented" in new WebBrowser {
      go to ValtechRadioPage
      page.title should equal(ValtechRadioPage.title)
    }

    "redirects to the next page given valid input of private keeper" in new WebBrowser {
      ValtechRadioPage.navigate()
      page.title should equal("Success")
      page.source should include("Success - you selected a keeper type of Private")
    }

    "redirects to the next page given valid input of business keeper" in new WebBrowser {
      ValtechRadioPage.navigate(isPrivateOwner = false)
      page.title should equal("Success")
      page.source should include("Success - you selected a keeper type of Business")
    }
  }
}
