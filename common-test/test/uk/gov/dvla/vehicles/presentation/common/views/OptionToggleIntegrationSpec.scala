package uk.gov.dvla.vehicles.presentation.common.views

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{click, go,pageSource, pageTitle}
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, OptionTogglePage}
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle

class OptionToggleIntegrationSpec extends UiSpec with TestHarness {

  "present" should {
    "Display all the three testing components with the correct labels" in new WebBrowserWithJs {
      go to OptionTogglePage
      pageTitle should equal(OptionTogglePage.title)

      val textOptionGroup = OptionTogglePage.textRadioOption
      textOptionGroup.label.getText should equal("Do you want to enter a string?")
      textOptionGroup.radio.selection should equal(None)
      textOptionGroup.component.underlying.getText should equal("")

      val intRadioOption = OptionTogglePage.intRadioOption

      intRadioOption.radio.selection should equal(None)
      intRadioOption.label.getText should equal("Do you want to enter an Int?")
      intRadioOption.component.text should equal("")

      val dateOptionGroup = OptionTogglePage.dateRadioOption
      dateOptionGroup.radio.selection should equal(None)
      dateOptionGroup.label.getText should equal("Do you want to enter a Date?")
      val dateComponent = dateOptionGroup.component
      dateComponent.day.text should equal("")
      dateComponent.month.text should equal("")
      dateComponent.year.text should equal("")
    }

    "show working option toggle" in new WebBrowserWithJs {
      go to OptionTogglePage
      pageTitle should equal(OptionTogglePage.title)

      OptionTogglePage.textRadioOption.assetComponentInvisible
      OptionTogglePage.textRadioOption.radio.value = "visible"
      OptionTogglePage.textRadioOption.assetComponentVisible
      OptionTogglePage.textRadioOption.radio.value = "invisible"
      OptionTogglePage.textRadioOption.assetComponentInvisible
    }

    "show/hide the options when yes/no is clicked" in new WebBrowserWithJs {
      go to OptionTogglePage

      OptionTogglePage.waitUntilJavascriptReady
      OptionTogglePage.textRadioOption.assetComponentInvisible

      click on OptionTogglePage.textRadioOption.radioInputWithValue(OptionalToggle.Visible)
      OptionTogglePage.textRadioOption.assetComponentVisible

      click on OptionTogglePage.textRadioOption.radioInputWithValue(OptionalToggle.Invisible)
      OptionTogglePage.textRadioOption.assetComponentInvisible
    }
  }

  "submit" should {
    "should remember the radio state" in new WebBrowserWithJs {
      go to OptionTogglePage

      OptionTogglePage.textRadioOption.radio.value = OptionalToggle.Visible
      OptionTogglePage.dateRadioOption.radio.value = OptionalToggle.Visible
      click on OptionTogglePage.submit

      OptionTogglePage.textRadioOption.radio.selection should equal(Some(OptionalToggle.Visible))
      OptionTogglePage.intRadioOption.radio.selection should equal(None)
      OptionTogglePage.dateRadioOption.radio.selection should equal(Some(OptionalToggle.Visible))
    }

    "should require options to be selected" in new WebBrowserWithJs {
      go to OptionTogglePage

      click on OptionTogglePage.submit
      verifyErrors(
        "optional-string-option - mandatory-alternative.not-selected",
        "optional-int-option - mandatory-alternative.not-selected",
        "optional-date-option - mandatory-alternative.not-selected"
      )

      val textOptionGroup = OptionTogglePage.textRadioOption
      textOptionGroup.radio.value = OptionalToggle.Visible
      textOptionGroup.assetComponentVisible
      textOptionGroup.component.value = "some text"
      click on OptionTogglePage.submit

      verifyErrors(
        "optional-int-option - mandatory-alternative.not-selected",
        "optional-date-option - mandatory-alternative.not-selected"
      )
    }

    "should run the underlying validations" in new WebBrowserWithJs {
      go to OptionTogglePage

      OptionTogglePage.textRadioOption.radio.value = OptionalToggle.Invisible
      OptionTogglePage.intRadioOption.radio.value = OptionalToggle.Invisible
      OptionTogglePage.dateRadioOption.radio.value = OptionalToggle.Visible
      click on OptionTogglePage.submit
      verifyErrors(
        "optional-date - Please enter a valid date in the format DD MM YYYY for example 20 3 1976 or 1 03 1976"
      )
      OptionTogglePage.dateRadioOption.assetComponentVisible
      OptionTogglePage.dateRadioOption.component.day.value = "12"
      click on OptionTogglePage.submit
      verifyErrors(
        "optional-date - Please enter a valid date in the format DD MM YYYY for example 20 3 1976 or 1 03 1976"
      )
      OptionTogglePage.assetComponentVisible("#optional-date_day")
      val dateComponent = OptionTogglePage.dateRadioOption.component
      dateComponent.day.value should equal("12")
      dateComponent.month.value should equal("")
      dateComponent.year.value should equal("")

      dateComponent.month.value = "12"
      dateComponent.year.value = "2012"

      click on OptionTogglePage.submit
      pageTitle should equal("Success")
      pageSource should include("Some(2012-12-12)")
    }
  }

  private def verifyErrors(errors: String*)(implicit driver: WebDriver): Unit = {
    val errorsStr = ErrorPanel.text
    errors.foreach(errorsStr should include(_))
    ErrorPanel.numberOfErrors should equal(errors.length)
  }
}