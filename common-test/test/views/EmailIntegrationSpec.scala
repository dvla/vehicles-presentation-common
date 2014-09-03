package views

import helpers.webbrowser.TestHarness
import helpers.UiSpec
import pages.{ErrorPanel, EmailPage}

class EmailIntegrationSpec extends UiSpec with TestHarness {

  "Email integration " should {
    "display the page" in new WebBrowser {
      go to EmailPage
      page.title should equal(EmailPage.title)
    }

    "display success message when correct data is entered" in new WebBrowser {
      EmailPage.navigate()
      page.title should equal("Success")
    }

    "display one validation error message when an email containing an incorrect format" in new WebBrowser {
      EmailPage.navigate(email = "qwerty qwerty")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
