package uk.gov.dvla.vehicles.presentation.common.views

import org.openqa.selenium.SearchContext
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, OptionTogglePage}

class OptionToggleIntegrationSpec extends UiSpec with TestHarness {

  "present" should {
    "Display all the three testing components with the correct labels" in new WebBrowser {
      go to OptionTogglePage
      page.title should equal(OptionTogglePage.title)

      val textRadio = OptionTogglePage.textRadio
      textRadio.radio.selection should equal(None)
      textRadio.label.text should equal("Do you want to enter a string?")
      textRadio.component.text should equal("")

      val intRadio = OptionTogglePage.intRadio
      intRadio.radio.selection should equal(None)
      intRadio.label.text should equal("Do you want to enter an Int?")
      intRadio.component.text should equal("")

      val dateRadio = OptionTogglePage.dateRadio
      dateRadio.radio.selection should equal(None)
      dateRadio.label.text should equal("Do you want to enter a Date?")
      val dateComponent = dateRadio.component
      dateComponent.day.text should equal("")
      dateComponent.month.text should equal("")
      dateComponent.year.text should equal("")
    }
  }

  "submit" should {
    "should remember the radio state" in new WebBrowser {
      go to OptionTogglePage

      OptionTogglePage.textRadio.radio.value = OptionalToggle.Visible
      OptionTogglePage.dateRadio.radio.value = OptionalToggle.Visible
      click on OptionTogglePage.submit

      OptionTogglePage.textRadio.radio.selection should equal(Some(OptionalToggle.Visible))
      OptionTogglePage.intRadio.radio.selection should equal(None)
      OptionTogglePage.dateRadio.radio.selection should equal(Some(OptionalToggle.Visible))
    }

    "should require options to be selected" in new WebBrowser {
      go to OptionTogglePage

      click on OptionTogglePage.submit
      verifyErrors(
        "optional-string-option - mandatory-alternative.not-selected",
        "optional-int-option - mandatory-alternative.not-selected",
        "optional-date-option - mandatory-alternative.not-selected"
      )

      val textRadio = OptionTogglePage.textRadio
      textRadio.radio.value = OptionalToggle.Visible
      textRadio.component.value = "some text"
      click on OptionTogglePage.submit

      verifyErrors(
        "optional-int-option - mandatory-alternative.not-selected",
        "optional-date-option - mandatory-alternative.not-selected"
      )
    }

    "should run the underlying validations" in new WebBrowser {
      go to OptionTogglePage

      OptionTogglePage.textRadio.radio.value = OptionalToggle.Invisible
      OptionTogglePage.intRadio.radio.value = OptionalToggle.Invisible
      OptionTogglePage.dateRadio.radio.value = OptionalToggle.Visible
      click on OptionTogglePage.submit
      verifyErrors(
        "optional-date - Please enter a valid date in the format DD MM YYYY for example 20 3 1976 or 1 03 1976"
      )

      OptionTogglePage.dateRadio.component.day.value = "12"
      click on OptionTogglePage.submit
      verifyErrors(
        "optional-date - Please enter a valid date in the format DD MM YYYY for example 20 3 1976 or 1 03 1976"
      )
      val dateComponent = OptionTogglePage.dateRadio.component
      dateComponent.day.value should equal("12")
      dateComponent.month.value should equal("")
      dateComponent.year.value should equal("")

      dateComponent.month.value = "12"
      dateComponent.year.value = "2012"

      click on OptionTogglePage.submit
      page.title should equal("Success")
      page.text should include("Some(2012-12-12)")

    }
  }

  private def verifyErrors(errors: String*)(implicit driver: SearchContext): Unit = {
    val errorsStr = ErrorPanel.text
    errors.foreach(errorsStr should include(_))
    ErrorPanel.numberOfErrors should equal(errors.length)
  }
}
