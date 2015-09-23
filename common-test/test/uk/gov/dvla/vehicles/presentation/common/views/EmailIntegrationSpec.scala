package uk.gov.dvla.vehicles.presentation.common.views

import org.openqa.selenium.Keys
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{EmailPage, ErrorPanel}

class EmailIntegrationSpec extends UiSpec with TestHarness {

  "Email integration " should {
    "display the page" in new WebBrowserForSelenium {
      go to EmailPage
      pageTitle should equal(EmailPage.title)
    }

    "display success message when correct data is entered" in new WebBrowserForSelenium {
      EmailPage.navigate()
      pageTitle should equal("Success")
    }

    "display one validation error message when an email containing an incorrect format" in new WebBrowserForSelenium {
      EmailPage.navigate(email = "qwerty qwerty")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "submit an invalid email and then submit a valid email" in new WebBrowserForSelenium {
      go to EmailPage
      EmailPage.businessEmail.value = "abc"
      EmailPage.businessEmailVerify.value = "abc"
      click on EmailPage.submit
      ErrorPanel.text should include(Messages("error.email"))
      ErrorPanel.numberOfErrors should equal(1)
      EmailPage.businessEmail.value = "abc@xyz.com"
      EmailPage.businessEmailVerify.value = "abc@xyz.com"
      click on EmailPage.submit
      webDriver.getPageSource should include("success - you entered an email Some(abc@xyz.com)")
    }

    "detect two different email addresses" in new WebBrowserForSelenium {
      go to EmailPage
      EmailPage.businessEmail.value = "abc@xyz.com"
      EmailPage.businessEmailVerify.value = "xyz@abc.com"
      click on EmailPage.submit
      ErrorPanel.text should include(Messages("error.email.not.match"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "prevent copy paste in the confirm field" in new WebBrowserForSelenium(webDriver = WebDriverFactory.defaultBrowserPhantomJs) {
      go to EmailPage
      EmailPage.businessEmail.value = "abc@xyz.com"
      EmailPage.businessEmail.underlying.sendKeys(Keys.LEFT_CONTROL + "a")
      EmailPage.businessEmail.underlying.sendKeys(Keys.LEFT_CONTROL + "c")
      click on EmailPage.businessEmailVerify
      EmailPage.businessEmailVerify.value = "old value"
      EmailPage.businessEmailVerify.underlying.sendKeys(Keys.LEFT_CONTROL + "v")
      EmailPage.businessEmailVerify.value should equal("old value")
    }
  }
}
