package views

import helpers.UiSpec
import helpers.webbrowser.TestHarness
import pages.{ErrorPanel, TitlePickerPage}
import play.api.i18n.Messages

class TitlePickerSpec extends UiSpec with TestHarness {
  "Title picker field controls" should {
    "Be visible" in new WebBrowser {
      val page = new TitlePickerPage
      go to page

      page.titlePicker.assertEnabled().assertNothingSelected()
    }
  }

  "Title picker validation" should {
    "Happy path" in new WebBrowser {
      var p = TitlePickerPage.navigate("mr", "")
      p.titlePicker.assertEnabled().assertSelected("mr")
      click on p.submit
      page.title should equal("Success")

      p = TitlePickerPage.navigate("miss", "")
      p.titlePicker.assertEnabled().assertSelected("miss")
      click on p.submit
      page.title should equal("Success")

      p = TitlePickerPage.navigate("mrs", "")
      p.titlePicker.assertEnabled().assertSelected("mrs")
      click on p.submit
      page.title should equal("Success")

      p = TitlePickerPage.navigate("other", "test")
      p.titlePicker.assertEnabled().assertSelected("other")
      click on p.submit
      page.title should equal("Success")
    }

    "Validate required option field" in new WebBrowser {
      val p = new TitlePickerPage
      go to p
      click on p.submit
      ErrorPanel.text should include(Messages("error.title.unknownOption"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate required other field" in new WebBrowser {
      val p = new TitlePickerPage
      go to p
      click on p.titlePicker.other
      click on p.submit
      ErrorPanel.text should include(Messages("error.title.missing"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate cannot enter more then 12 characters in other field" in new WebBrowser {
      val p = new TitlePickerPage
      go to p
      click on p.titlePicker.other
      p.titlePicker.otherText.value = "asdfghjklQWER"
      click on p.submit
      ErrorPanel.text should include(Messages("error.title.tooLong"))
      ErrorPanel.numberOfErrors should equal(1)
    }

    "Validate non alphabetical character other input" in new WebBrowser {
      val p = new TitlePickerPage
      go to p
      click on p.titlePicker.other
      p.titlePicker.otherText.value = "&^^%"
      click on p.submit
      ErrorPanel.text should include(Messages("error.title.illegalCharacters"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
