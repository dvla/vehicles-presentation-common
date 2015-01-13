package uk.gov.dvla.vehicles.presentation.common.views.helpers

import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required.RequiredField
import uk.gov.dvla.vehicles.presentation.common.views.helpers.HtmlArgsExtensions.RichHtmlArgs

final class HtmlArgsExtensionsSpec extends UnitSpec {

  "withMaxLength" should {
    "return the same args when key 'maxLength' is already present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithMaxLength)
      // Override validationOff to check the behaviour of the production code.
      val result = richHtmlArgs.withMaxLength

      result should equal(htmlArgsWithMaxLength)
    }

    "add key 'maxLength' with default value when not present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)
      // Override validationOff to check the behaviour of the production code.
      val result = richHtmlArgs.withMaxLength

      result should equal(htmlArgsWithMaxLength)
    }
  }

  "withoutAutoComplete" should {

    "add key-value 'autocomplete' 'off' attribute when key is not present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.withoutAutoComplete

      result should equal(htmlArgsWithAutoCompleteOff)
    }

    "return the same args when key-value 'autocomplete' 'off' is present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithAutoCompleteOff)

      val result = richHtmlArgs.withoutAutoComplete

      result should equal(htmlArgsWithAutoCompleteOff)
    }

    "replace key-value autocomplete 'on' with autocomplete 'off'" in {
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

  "valueElseTrue" should {
    "add the key-value 'value' with default value 'true' when value is not present" in {
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.valueElseTrue

      val htmlArgsWithValueDefault = Map('title -> "test", 'value -> true)
      result should equal(htmlArgsWithValueDefault)
    }

    "return the same when key 'value' is present" in {
      val htmlArgsWithValue = Map('title -> "test", 'value -> "test-value")
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithValue)

      val result = richHtmlArgs.valueElseTrue

      result should equal(htmlArgsWithValue)
    }
  }

  "checkedWhenValueMatches" should {
    "return the same when value not present in htmlArgs" in {
      val fieldValue = Some("test-value")
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.checkedWhenValueMatches(fieldValue)

      result should equal(htmlArgsMinimal)
    }

    "return the same when field has a value doesn't match htmlArgs value" in {
      val fieldValue = Some("test-value")
      val htmlArgsWithDifferentValue = Map('title -> "test", 'value -> "different-test-value")
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithDifferentValue)

      val result = richHtmlArgs.checkedWhenValueMatches(fieldValue)

      result should equal(htmlArgsWithDifferentValue)
    }

    "return the same when field and htmlArgs have no 'checked' value" in {
      val fieldValue = None
      val richHtmlArgs = new RichHtmlArgs(htmlArgsMinimal)

      val result = richHtmlArgs.checkedWhenValueMatches(fieldValue)

      result should equal(htmlArgsMinimal)
    }

    "return the same when field has no value" in {
      val fieldValue = None
      val htmlArgsWithValue = Map('title -> "test", 'value -> "test-value")
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithValue)

      val result = richHtmlArgs.checkedWhenValueMatches(fieldValue)

      result should equal(htmlArgsWithValue)
    }

//    "add 'checked' when the field has no value (so defaults to 'true') and htmlArgs contains value 'true'" in {
//      val fieldValue = None
//      val htmlArgsWithValue = Map('title -> "test", 'value -> true)
//      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithValue)
//
//      val result = richHtmlArgs.checkedWhenValueMatches(fieldValue)
//
//      val htmlArgsWithChecked = Map('title -> "test", 'value -> true, 'checked -> "")
//      result should equal(htmlArgsWithChecked)
//    }

    "add 'checked' when the field has the same value as the htmlArgs value" in {
      val fieldValue = Some("test-value")
      val htmlArgsWithSameValue = Map('title -> "test", 'value -> "test-value")
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithSameValue)

      val result = richHtmlArgs.checkedWhenValueMatches(fieldValue)

      val htmlArgsWithChecked = Map('title -> "test", 'value -> "test-value", 'checked -> "")
      result should equal(htmlArgsWithChecked)
    }

    "add 'checked' when the field has the same value as the htmlArgs value (specified as a boolean)" in {
      val fieldValue = Some("true")
      val htmlArgsWithSameValue = Map('title -> "test", 'value -> true)
      val richHtmlArgs = new RichHtmlArgs(htmlArgsWithSameValue)

      val result = richHtmlArgs.checkedWhenValueMatches(fieldValue)

      val htmlArgsWithChecked = Map('title -> "test", 'value -> true, 'checked -> "")
      result should equal(htmlArgsWithChecked)
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