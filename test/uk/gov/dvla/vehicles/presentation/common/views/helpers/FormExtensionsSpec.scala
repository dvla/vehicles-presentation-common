package uk.gov.dvla.vehicles.presentation.common.views.helpers

import play.api.data.Form
import play.api.data.FormError
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.text
import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.helpers.FormExtensions.formBinding
import uk.gov.dvla.vehicles.presentation.common.views.helpers.FormExtensions.textWithTransform
import uk.gov.dvla.vehicles.presentation.common.views.helpers.FormExtensions.trimmedText

final class FormExtensionsSpec extends UnitSpec {

  "anyMandatoryFields" should {
    "return true when the required constraint is on a field" in {
      val formWithMandatory = Form(
        mapping(
          "id1" -> text(),
          "id2" -> nonEmptyText(),
          "id3" -> text()
        )(Model.apply)(Model.unapply)
      )

      formWithMandatory.anyMandatoryFields should equal(true)
    }

    "return false when the required constraint is not on any field" in {
      val formWithoutMandatory = createForm()
      formWithoutMandatory.anyMandatoryFields should equal(false)
    }
  }

  "replaceError (key only)" should {
    "return an unchanged form when matching key is not found in errors list" in {
      val form = createForm(errors = Seq(originalError))
      val idToReplace = errorIdNotInList

      // It has 1 error but the id of the replacement is different so there should be no change.
      val result = form.replaceError(idToReplace, replacementError)

      result should equal(form)
    }

    "return form with replaced error message when matching key is found in errors list" in {
      val form = createForm(errors = Seq(originalError))
      val expectedForm = createForm(errors = Seq(replacementError))
      val idToReplace = originalErrorId

      // It has 1 error that matches this id so it should be detected and changed.
      val result = form.replaceError(key = idToReplace, replacementError)

      result should equal(expectedForm)
    }

    "return form with one replaced error message when matching key is found more than once in errors list" in {
      val form = createForm(errors = Seq(originalError, originalError, originalError))
      val expectedForm = createForm(errors = Seq(replacementError))
      val idToReplace = originalErrorId

      // It has errors that match this id so they should be detected and changed.
      val result = form.replaceError(key = idToReplace, replacementError)

      result should equal(expectedForm)
    }

    "return form with one replaced error message when matching key is found in list with different errors" in {
      val form = createForm(errors = Seq(differentError1, originalError))
      val expectedForm = createForm(errors = Seq(differentError1, replacementError))
      val idToReplace = originalErrorId

      // It has errors that match this id so they should be detected and changed.
      val result = form.replaceError(key = idToReplace, replacementError)

      result should equal(expectedForm)
    }

    "removes duplicates of the replaced error" in {
      val form = createForm(errors = Seq(differentError1, originalError, originalError, differentError2))
      val expectedForm = createForm(errors = Seq(differentError1, replacementError, differentError2))
      val idToReplace = originalErrorId

      // It has errors that match this id so they should be detected and replaced with just one error message
      val result = form.replaceError(key = idToReplace, replacementError)

      result should equal(expectedForm)
    }

    "preserve the order of the error messages when replacing" in {
      val form = createForm(errors = Seq(differentError1, originalError, differentError2))
      val expectedForm = createForm(errors = Seq(differentError1, replacementError, differentError2))
      val idToReplace = originalErrorId

      // It has errors that match this id so they should be detected and changed in-place without changing the order.
      val result = form.replaceError(key = idToReplace, replacementError)

      result should equal(expectedForm)
    }
  }

  "replaceError (key and message)" should {
    "return an unchanged form when matching key not found in errors list" in {
      val form = createForm(errors = Seq(originalError))
      val idToReplace = errorIdNotInList

      // It has 1 error but the id of the replacement is different so there should be no change.
      val result = form.replaceError(key = idToReplace, originalErrorMessage, replacementError)

      result should equal(form)
    }

    "return an unchanged form when matching key is found but message is not found in errors list" in {
      val form = createForm(errors = Seq(originalError))
      val idToReplace = originalErrorId

      // It has 1 error but the message of the replacement is different so there should be no change.
      val result = form.replaceError(key = idToReplace, "error.different", replacementError)

      result should equal(form)
    }

    "return form with replaced error message when matching key and message are found in errors list" in {
      val form = createForm(errors = Seq(originalError))
      val expectedForm = createForm(errors = Seq(replacementError))
      val idToReplace = originalErrorId

      // It has 1 error that matches this id so it should be detected and changed.
      val result = form.replaceError(key = idToReplace, originalErrorMessage, replacementError)

      result should equal(expectedForm)
    }

    "return form with one replaced error message when matching key and message is found more than once in errors list" in {
      val form = createForm(errors = Seq(originalError, originalError, originalError))
      val expectedForm = createForm(errors = Seq(replacementError))
      val idToReplace = originalErrorId

      // It has errors that match this id so they should be detected and changed.
      val result = form.replaceError(key = idToReplace, originalErrorMessage, replacementError)

      result should equal(expectedForm)
    }

    "return form with one replaced error message when matching key and message is found in list with different errors" in {
      val form = createForm(errors = Seq(differentError1, originalError))
      val expectedForm = createForm(errors = Seq(differentError1, replacementError))
      val idToReplace = originalErrorId

      // It has errors that match this id and message so they should be detected and changed.
      val result = form.replaceError(key = idToReplace, originalErrorMessage, replacementError)

      result should equal(expectedForm)
    }

    "removes duplicates of the replaced error" in {
      val form = createForm(errors = Seq(differentError1, originalError, originalError, differentError2))
      val expectedForm = createForm(errors = Seq(differentError1, replacementError, differentError2))
      val idToReplace = originalErrorId

      // It has errors that match this id and message so they should be detected and replaced with just one error message.
      val result = form.replaceError(key = idToReplace, originalErrorMessage, replacementError)

      result should equal(expectedForm)
    }

    "preserve the order of the error messages when replacing" in {
      val form = createForm(errors = Seq(differentError1, originalError, differentError2))
      val expectedForm = createForm(errors = Seq(differentError1, replacementError, differentError2))
      val idToReplace = originalErrorId

      // It has errors that match this id so they should be detected and changed in-place without changing the order.
      val result = form.replaceError(key = idToReplace, originalErrorMessage, replacementError)

      result should equal(expectedForm)
    }
  }

  "distinctErrors" should {
    "return an unchanged form when no duplicates present" in {
      val form = createForm(errors = Seq(originalError))
      form.distinctErrors should equal(form)
    }

    "return form with duplicates removed when duplicates are present" in {
      val form = createForm(errors = Seq(originalError, originalError, originalError))
      val expectedForm = createForm(errors = Seq(originalError))
      form.distinctErrors should equal(expectedForm)
    }
  }

  "trimmed text mapping" should {

    "remove leading and trailing spaces, carriage returns and line feeds by default" in {
      val form = Form(
        "value" -> trimmedText()
      ).bind(Map("value" -> " \n\r foo  \r\n "))

      form.hasErrors should equal(false)
      form.get should equal("foo")
    }

    "not remove commas by default" in {
      val form = Form(
        "value" -> trimmedText()
      ).bind(Map("value" -> ",foo,"))

      form.hasErrors should equal(false)
      form.get should equal(",foo,")
    }

    "trim characters provided as additional arguments" in {
      val form = Form(
        "value" -> trimmedText(additionalTrimChars = Seq(','))
      ).bind(Map("value" -> ",foo,"))

      form.hasErrors should equal(false)
      form.get should equal("foo")
    }

    "exclude trimmed characters from the min length" in {
      val validForm = Form(
        "value" -> trimmedText(minLength = 3)
      ).bind(Map("value" -> "  foo  "))

      validForm.errors.length should equal(0)

      val invalidForm = Form(
        "value" -> trimmedText(minLength = 3)
      ).bind(Map("value" -> "  fo   "))

      invalidForm.errors.length should equal(1)
    }

    "exclude trimmed characters from the max length" in {
      val validForm = Form(
        "value" -> trimmedText(maxLength = 3)
      ).bind(Map("value" -> "  foo  "))

      validForm.errors.length should equal(0)

      val invalidForm = Form(
        "value" -> trimmedText(maxLength = 3)
      ).bind(Map("value" -> "  foob   "))

      invalidForm.errors.length should equal(1)
    }
  }

  "transformer" should {

    "uppercase test" in {
      val form = Form(
        "value" -> textWithTransform(_.toUpperCase.trim)()
      ).bind(Map("value" -> "foo  "))

      form.hasErrors should equal(false)
      form.get should equal("FOO")
    }

    "applies length validation after transform" in {
      val form = Form(
        "value" -> textWithTransform { s => ""}(minLength = 1)
      ).bind(Map("value" -> "foo  "))

      form.hasErrors should equal(true)
      form.errors(0).message should equal("error.minLength")
    }
  }

  private final case class Model(id1: String, id2: String, id3: String)

  private val formMappingWithoutMandatory: Mapping[Model] = mapping(
    "id1" -> text(),
    "id2" -> text(),
    "id3" -> text()
  )(Model.apply)(Model.unapply)
  private val originalErrorId = "id1"
  private val originalErrorMessage = "error.original"
  private val errorIdNotInList = "id666"
  private val originalError: FormError = FormError(key = originalErrorId, message = originalErrorMessage, args = Seq.empty)
  private val differentError1: FormError = FormError(key = "id777", message = originalErrorMessage, args = Seq.empty)
  private val differentError2: FormError = FormError(key = "id888", message = originalErrorMessage, args = Seq.empty)
  // The id used in the replacement error can be either:
  // 1) the same as the original error.
  // 2) a different id in the case that you are swapping an error caused in a sub-component of a compound mapping
  // with the root id of the compound.
  private val replacementError: FormError = FormError(key = "id-same-or-replacement", message = "error.replacement", args = Seq.empty)

  private def createForm(errors: Seq[FormError] = Seq.empty) = new Form(
    mapping = formMappingWithoutMandatory,
    data = Map.empty,
    errors = errors,
    value = None
  )
}