package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, TitlePickerPage}

class TitlePickerSpec extends UiSpec with TestHarness {
  "Title picker field controls" should {
    "Be visible" in new WebBrowserForSelenium {
      go to TitlePickerPage

      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertNothingSelected()
//      TitlePickerPage.mr.underlying should equal(activeElementUnderlying)
//      def activeElementUnderlying(implicit driver: WebDriver) = activeElement.switch(driver)
    }
  }

  "Title picker validation" should {
    "Happy path" in new WebBrowserForSelenium {
      TitlePickerPage.navigate("mr", "")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("mr")
      click on TitlePickerPage.submit
      pageTitle should equal("Success")

      TitlePickerPage.navigate("miss", "")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("miss")
      click on TitlePickerPage.submit
      pageTitle should equal("Success")

      TitlePickerPage.navigate("mrs", "")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("mrs")
      click on TitlePickerPage.submit
      pageTitle should equal("Success")

      TitlePickerPage.navigate("other", "test")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("other")
      click on TitlePickerPage.submit
      pageTitle should equal("Success")
    }

    "Validate required option field" in new WebBrowserForSelenium {
      go to TitlePickerPage
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.unknownOption"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate required other field" in new WebBrowserForSelenium {
      go to TitlePickerPage
      click on TitlePickerPage.other
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.missing"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate cannot enter more then 12 characters in other field" in new WebBrowserForSelenium {
      go to TitlePickerPage
      click on TitlePickerPage.other
      TitlePickerPage.otherText.value = "asdfghjklQWER"
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.tooLong"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate non alphabetical character other input" in new WebBrowserForSelenium {
      go to TitlePickerPage
      click on TitlePickerPage.other
      TitlePickerPage.otherText.value = "&^^%"
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.illegalCharacters"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  "Javascript enabled" should {
    "still have other text box visible after error" in new WebBrowserWithJs {
      go to TitlePickerPage
      click on TitlePickerPage.other
      click on TitlePickerPage.submit
      ErrorPanel.numberOfErrors should equal(1)
      TitlePickerPage.otherText.isDisplayed shouldBe (true)
    }
  }
}
