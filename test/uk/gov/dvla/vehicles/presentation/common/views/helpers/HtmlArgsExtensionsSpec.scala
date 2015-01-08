package uk.gov.dvla.vehicles.presentation.common.views.helpers

import uk.gov.dvla.vehicles.presentation.common.views.helpers.HtmlArgsExtensions.RichHtmlArgs
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, WithApplication}

final class HtmlArgsExtensionsSpec extends UnitSpec {

  "withMaxLength" should {
    "return the same args when key maxLength is already present" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithMaxLength)
      // Override validationOff to check the behaviour of the production code.
      val result = richHtmlArgs.withMaxLength

      result should equal(htmlArgsWithMaxLength)
    }

    "add key maxLength with default value to args not present" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      // Override validationOff to check the behaviour of the production code.
      val result = richHtmlArgs.withMaxLength

      result should equal(htmlArgsWithMaxLength)
    }
  }

  "withoutAutoComplete" should {
    val htmlArgsMinimal: Map[Symbol, Any] = Map('title -> "test")
    val htmlArgsWithAutoCompleteOff: Map[Symbol, Any] = Map('title -> "test", 'autocomplete -> "off")
    val htmlArgsWithAutoCompleteOn: Map[Symbol, Any] = Map('title -> "test", 'autocomplete -> "on")

    "add autocomplete off when key is not present" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.withoutAutoComplete

      result should equal(htmlArgsWithAutoCompleteOff)
    }

    "return the same args when key-value autocomplete 'off' is present" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithAutoCompleteOff)

      val result = richHtmlArgs.withoutAutoComplete

      result should equal(htmlArgsWithAutoCompleteOff)
    }

    "replace key-value autocomplete 'on' with autocomplete 'off'" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithAutoCompleteOn)

      val result = richHtmlArgs.withoutAutoComplete

      result should equal(htmlArgsWithAutoCompleteOff)
    }
  }

  private val htmlArgsMinimal: Map[Symbol, Any] = Map('title -> "test")
  private val htmlArgsWithMaxLength: Map[Symbol, Any] = Map('title -> "test", 'maxLength -> 60)
}