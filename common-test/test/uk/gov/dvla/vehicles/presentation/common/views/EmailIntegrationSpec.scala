package uk.gov.dvla.vehicles.presentation.common.views

import org.openqa.selenium.Keys
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{EmailPage, ErrorPanel}

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

    "submit an invalid email and then submit a valid email" in new WebBrowser {
      go to EmailPage
      EmailPage.businessEmail enter "abc"
      EmailPage.businessEmailVerify enter "abc"
      click on EmailPage.submit
      ErrorPanel.text should include(Messages("error.email"))
      ErrorPanel.numberOfErrors should equal(1)
      EmailPage.businessEmail enter "abc@xyz.com"
      EmailPage.businessEmailVerify enter "abc@xyz.com"
      click on EmailPage.submit
      webDriver.getPageSource should include("success - you entered an email Some(abc@xyz.com)")
    }

    "detect two different email addresses" in new WebBrowser {
      go to EmailPage
      EmailPage.businessEmail enter "abc@xyz.com"
      EmailPage.businessEmailVerify enter "xyz@abc.com"
      click on EmailPage.submit
      ErrorPanel.text should include(Messages("error.email.not.match"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "prevent copy paste in the confirm field" in new WebBrowser(webDriver = WebDriverFactory.defaultBrowserPhantomJs) {
      go to EmailPage
      EmailPage.businessEmail enter "abc@xyz.com"
      EmailPage.businessEmail.underlying.sendKeys(Keys.LEFT_CONTROL + "a")
      EmailPage.businessEmail.underlying.sendKeys(Keys.LEFT_CONTROL + "c")
      click on EmailPage.businessEmailVerify
      EmailPage.businessEmailVerify.value = "old value"
      EmailPage.businessEmailVerify.underlying.sendKeys(Keys.LEFT_CONTROL + "v")
      EmailPage.businessEmailVerify.value should equal("old value")
    }
  }
}
