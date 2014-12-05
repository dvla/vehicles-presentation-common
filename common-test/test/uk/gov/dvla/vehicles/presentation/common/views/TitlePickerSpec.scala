package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.pages

import common.helpers.UiSpec
//import org.openqa.selenium.WebDriver
import pages.{TitlePickerPage, ErrorPanel}
import play.api.i18n.Messages

class TitlePickerSpec extends UiSpec with TestHarness {
  "Title picker field controls" should {
    "Be visible" in new WebBrowser {
      go to TitlePickerPage

      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertNothingSelected()
//      TitlePickerPage.mr.underlying should equal(activeElementUnderlying)
//      def activeElementUnderlying(implicit driver: WebDriver) = activeElement.switch(driver)
    }
  }

  "Title picker validation" should {
    "Happy path" in new WebBrowser {
      TitlePickerPage.navigate("mr", "")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("mr")
      click on TitlePickerPage.submit
      page.title should equal("Success")

      TitlePickerPage.navigate("miss", "")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("miss")
      click on TitlePickerPage.submit
      page.title should equal("Success")

      TitlePickerPage.navigate("mrs", "")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("mrs")
      click on TitlePickerPage.submit
      page.title should equal("Success")

      TitlePickerPage.navigate("other", "test")
      TitlePickerPage.assertEnabled()
      TitlePickerPage.assertSelected("other")
      click on TitlePickerPage.submit
      page.title should equal("Success")
    }

    "Validate required option field" in new WebBrowser {
      go to TitlePickerPage
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.unknownOption"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate required other field" in new WebBrowser {
      go to TitlePickerPage
      click on TitlePickerPage.other
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.missing"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate cannot enter more then 12 characters in other field" in new WebBrowser {
      go to TitlePickerPage
      click on TitlePickerPage.other
      TitlePickerPage.otherText.value = "asdfghjklQWER"
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.tooLong"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate non alphabetical character other input" in new WebBrowser {
      go to TitlePickerPage
      click on TitlePickerPage.other
      TitlePickerPage.otherText.value = "&^^%"
      click on TitlePickerPage.submit
      ErrorPanel.text should include(Messages("error.title.illegalCharacters"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
