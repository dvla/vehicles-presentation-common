package uk.gov.dvla.vehicles.presentation.common.views

import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
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

    "detect two different email addresses" in new WebBrowser {
      go to EmailPage
      EmailPage.businessEmail enter "abc@xyz.com"
      EmailPage.businessEmailVerify enter "xyz@abc.com"
      click on EmailPage.submit
      ErrorPanel.text should include(Messages("error.email.not.match"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
