package views.disposal_of_vehicle

import helpers.UiSpec
import helpers.tags.UiTag
import helpers.webbrowser.TestHarness
import mappings.disposal_of_vehicle.SetupTradeDetails
import org.openqa.selenium.{WebElement, By}
import pages.common.Accessibility
import pages.common.ErrorPanel
import pages.disposal_of_vehicle.SetupTradeDetailsPage._
import pages.disposal_of_vehicle._
import helpers.disposal_of_vehicle.CookieFactoryForUISpecs
import pages.common.AlternateLanguages._

final class SetUpTradeDetailsIntegrationSpec extends UiSpec with TestHarness {
  "got to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to SetupTradeDetailsPage
      page.title should equal(SetupTradeDetailsPage.title)
    }

    "display the progress of the page" taggedAs UiTag in new WebBrowser {
      go to SetupTradeDetailsPage

      page.source.contains("Step 2 of 6") should equal(true)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to SetupTradeDetailsPage
      val csrf: WebElement = webDriver.findElement(By.name(services.csrf_prevention.CSRFPreventionAction.csrfPreventionTokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(services.csrf_prevention.CSRFPreventionAction.csrfPreventionTokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }

    "display the 'Cymraeg' language button and not the 'English' language button when the language cookie is set to 'en'" taggedAs UiTag ignore new WebBrowser {
      go to BeforeYouStartPage // By default will load in English.
      CookieFactoryForUISpecs.withLanguageEn()
      go to SetupTradeDetailsPage

      hasCymraeg should equal(true)
      hasEnglish should equal(false)
    }

    "display the 'English' language button and not the 'Cymraeg' language button when the language cookie is set to 'cy'" taggedAs UiTag ignore new WebBrowser {
      go to BeforeYouStartPage // By default will load in English.
      CookieFactoryForUISpecs.withLanguageCy()
      go to SetupTradeDetailsPage

      hasCymraeg should equal(false)
      hasEnglish should equal(true)
    }
  }

  "lookup button" should {
    "go to the next page when correct data is entered" taggedAs UiTag in new WebBrowser {
      happyPath()
      page.title should equal(BusinessChooseYourAddressPage.title)
    }

    "display two summary validation error messages when no details are entered" taggedAs UiTag in new WebBrowser {
      happyPath(traderBusinessName = "", traderBusinessPostcode = "")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "add aria required attribute to trader name field when required field not input" taggedAs UiTag in new WebBrowser {
      happyPath(traderBusinessName = "")
      Accessibility.ariaRequiredPresent(SetupTradeDetails.TraderNameId) should equal(true)
    }

    "add aria invalid attribute to trader name field when required field not input" taggedAs UiTag in new WebBrowser {
      happyPath(traderBusinessName = "")
      Accessibility.ariaInvalidPresent(SetupTradeDetails.TraderNameId) should equal(true)
    }

    "add aria required attribute to trader postcode field when required field not input" taggedAs UiTag in new WebBrowser {
      happyPath(traderBusinessPostcode = "")
      Accessibility.ariaRequiredPresent(SetupTradeDetails.TraderPostcodeId) should equal(true)
    }

    "add aria invalid attribute to trader postcode field when required field not input" taggedAs UiTag in new WebBrowser {
      happyPath(traderBusinessPostcode = "")
      Accessibility.ariaInvalidPresent(SetupTradeDetails.TraderPostcodeId) should equal(true)
    }
  }
}
