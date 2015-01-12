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

    "add autocomplete off attribute when key is not present" in new WithApplication {
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
      val htmlArgsWithAutoCompleteOn: Map[Symbol, Any] = Map('title -> "test", 'autocomplete -> "on")
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithAutoCompleteOn)

      val result = richHtmlArgs.withoutAutoComplete

      result should equal(htmlArgsWithAutoCompleteOff)
    }
  }

  "withAriaDescribedby" should {
    "return the same when hint text is not present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      val key = Symbol("aria-describedby")

      val result: Map[Symbol, Any] = richHtmlArgs.withAriaDescribedby(hintText = None, idOfRelatedField = "test-id")

      result.contains(key) should equal(false)
    }

    "add aria-describedby attribute when hint text is present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      val key = Symbol("aria-describedby")

      val result: Map[Symbol, Any] = richHtmlArgs.withAriaDescribedby(hintText = Some("test-hint-text"), idOfRelatedField = "test-id")

      result.contains(key) should equal(true)
      result.get(key) should equal(Some("test-id-hint"))
    }
  }

  private def htmlArgsMinimal: Map[Symbol, Any] = Map('title -> "test")

  private def htmlArgsWithMaxLength: Map[Symbol, Any] = Map('title -> "test", 'maxLength -> 60)

  private def htmlArgsWithAutoCompleteOff: Map[Symbol, Any] = Map('title -> "test", 'autocomplete -> "off")
}