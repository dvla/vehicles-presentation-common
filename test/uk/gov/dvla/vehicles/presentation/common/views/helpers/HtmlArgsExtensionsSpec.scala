package uk.gov.dvla.vehicles.presentation.common.views.helpers

import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required.RequiredField
import uk.gov.dvla.vehicles.presentation.common.views.helpers.HtmlArgsExtensions.RichHtmlArgs
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, WithApplication}

final class HtmlArgsExtensionsSpec extends UnitSpec {

  "withMaxLength" should {
    "return the same args when key 'maxLength' is already present" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithMaxLength)
      // Override validationOff to check the behaviour of the production code.
      val result = richHtmlArgs.withMaxLength

      result should equal(htmlArgsWithMaxLength)
    }

    "add key 'maxLength' with default value to args not present" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      // Override validationOff to check the behaviour of the production code.
      val result = richHtmlArgs.withMaxLength

      result should equal(htmlArgsWithMaxLength)
    }
  }

  "withoutAutoComplete" should {

    "add key-value 'autocomplete' 'off' attribute when key is not present" in new WithApplication {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.withoutAutoComplete

      result should equal(htmlArgsWithAutoCompleteOff)
    }

    "return the same args when key-value 'autocomplete' 'off' is present" in new WithApplication {
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

    "add 'aria-describedby' attribute when hint text is present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      val key = Symbol("aria-describedby")

      val result: Map[Symbol, Any] = richHtmlArgs.withAriaDescribedby(hintText = Some("test-hint-text"), idOfRelatedField = "test-id")

      result.contains(key) should equal(true)
      result.get(key) should equal(Some("test-id-hint"))
    }
  }

  "withTypeAttribute" should {
    "add 'type=text' when key 'type' is not present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.withTypeAttribute

      val htmlArgsWithTypeText = Map('title -> "test", 'type -> "text")
      result should equal(htmlArgsWithTypeText)
    }

    "add expected when key 'typeTel' is present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeTel)

      val result = richHtmlArgs.withTypeAttribute

      val key = 'type
      val value = """tel"""
      result.contains(key)
      result.get(key) should equal(Some(value))
    }

    "remove key 'typeTel' when present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeTel)

      val result = richHtmlArgs.withTypeAttribute

      result.contains('typeTel) should equal(false)
    }

    "add expected when key 'typeFleetNumber' is present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeFleetNumber)

      val result = richHtmlArgs.withTypeAttribute

      val key = 'type
      val value = """tel"""
      result.contains(key)
      result.get(key) should equal(Some(value))
    }

    "remove key 'typeFleetNumber' when present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeFleetNumber)

      val result = richHtmlArgs.withTypeAttribute

      result.contains('typeFleetNumber) should equal(false)
    }

    "add expected when key 'typeEmail' is present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeEmail)

      val result = richHtmlArgs.withTypeAttribute

      val key = 'type
      val value = "email"
      result.contains(key)
      result.get(key) should equal(Some(value))
    }

    "remove key 'typeEmail' when present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeEmail)

      val result = richHtmlArgs.withTypeAttribute

      result.contains('typeEmail) should equal(false)
    }

    "add expected when key 'alphabeticalOnly' is present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeAlphabeticalOnly)

      val result = richHtmlArgs.withTypeAttribute

      val key = 'type
      val value = """text"""
      result.contains(key)
      result.get(key) should equal(Some(value))
    }

    "remove key 'alphabeticalOnly' when present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithTypeAlphabeticalOnly)

      val result = richHtmlArgs.withTypeAttribute

      result.contains('alphabeticalOnly) should equal(false)
    }
  }

  "withAriaInvalid" should {
    "return the same when hasErrors is false" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.withAriaInvalid(hasErrors = false)

      result should equal(htmlArgsMinimal)
    }

    "add aria-invalid when hasErrors is true" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.withAriaInvalid(hasErrors = true)

      val key = Symbol("aria-invalid")
      val htmlArgsWithAriaInvalid: Map[Symbol, Any] = Map('title -> "test", key -> true)
      result should equal(htmlArgsWithAriaInvalid)
    }
  }

  "withAriaRequired" should {
    "return the same when field does not have a required constraint" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      val constraints: Seq[(String, Seq[Any])] = Seq.empty

      val result = richHtmlArgs.withAriaRequired(constraints)

      result should equal(htmlArgsMinimal)
    }

    "add aria-required when field has a required constraint" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      val constraints: Seq[(String, Seq[Any])] = Seq((RequiredField, Seq()))

      val result = richHtmlArgs.withAriaRequired(constraints)

      val key = Symbol("aria-required")
      result should equal(Map('title -> "test", key -> true))
    }
  }

  private def htmlArgsMinimal: Map[Symbol, Any] = Map('title -> "test")

  private def htmlArgsWithMaxLength: Map[Symbol, Any] = Map('title -> "test", 'maxLength -> 60)

  private def htmlArgsWithAutoCompleteOff: Map[Symbol, Any] = Map('title -> "test", 'autocomplete -> "off")

  private def htmlArgsWithTypeTel = Map('title -> "test", 'typeTel -> true)

  private def htmlArgsWithTypeFleetNumber = Map('title -> "test", 'typeFleetNumber -> true)

  private def htmlArgsWithTypeEmail = Map('title -> "test", 'typeEmail -> true)

  private def htmlArgsWithTypeAlphabeticalOnly = Map('title -> "test", 'alphabeticalOnly -> true)
}