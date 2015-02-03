package uk.gov.dvla.vehicles.presentation.common.views.helpers

import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.validation.Constraints
import play.api.data.{Form, FormError, Mapping}
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required.RequiredField
import scala.language.implicitConversions

object FormExtensions {
  implicit def formBinding[T](form: Form[T]) = new RichForm[T](form)

  // TODO should this be updated to look like the extension method in EncryptedCookieImplicits?

  // Extension method for forms.
  class RichForm[T](form: Form[T]) {

    private def replaceError(newError: FormError, matcher: FormError => Boolean): Form[T] = {
      val errorToReplace = form.errors.find(matcher)
      errorToReplace match {
        case Some(n) => form.copy(errors = form.errors.filterNot(matcher)).withError(newError) // Replace the error we were looking for.
        case None => form
      }
    }

    def replaceError(key: String, newError: FormError): Form[T] = replaceError(newError, { e => e.key == key})

    def replaceError(key: String, message: String, newError: FormError): Form[T] =
      replaceError(newError, { e => e.key == key && e.message == message})

    def distinctErrors: Form[T] = form.copy(errors = form.errors.distinct)

    def anyMandatoryFields: Boolean =
      form.mapping.mappings.exists(m => m.constraints.exists(c => c.name == Some(RequiredField)))
  }

  def trimmedText(minLength: Int = 0,
                  maxLength: Int = Int.MaxValue,
                  additionalTrimChars: Seq[Char] = Nil): Mapping[String] =
    textWithTransform(trimWithAdditionalChars(_, additionalTrimChars))(minLength, maxLength)

  def textWithTransform(transform: String => String)
                       (minLength: Int = 0, maxLength: Int = Int.MaxValue): Mapping[String] = {
    val fieldMapping = of[String](transformedStringFormat(transform))

    (minLength, maxLength) match {
      case (0, Int.MaxValue) => fieldMapping
      case (min, Int.MaxValue) => fieldMapping verifying Constraints.minLength(min)
      case (0, max) => fieldMapping verifying Constraints.maxLength(max)
      case (min, max) => fieldMapping verifying(Constraints.minLength(min), Constraints.maxLength(max))
    }
  }

  def nonEmptyTextWithTransform(transform: String => String)
                               (minLength: Int = 0, maxLength: Int = Int.MaxValue): Mapping[String] =
    textWithTransform(transform)(minLength, maxLength) verifying Constraints.nonEmpty

  /**
   * The nonEmpty variant of trimmedText applies the additional constraint that the text is empty
   */
  def nonEmptyTrimmedText(minLength: Int = 0,
                          maxLength: Int = Int.MaxValue,
                          additionalTrimChars: Seq[Char] = Nil): Mapping[String] =
    trimmedText(minLength, maxLength, additionalTrimChars) verifying Constraints.nonEmpty

  /**
   * trim function that accepts additional chars to trim.
   * Code based on Scala's trim implementation (as it should be optimized)
   */
  def trimWithAdditionalChars(value: String, trimChars: Seq[Char]): String = {
    var len: Int = value.length
    var st: Int = 0
    val `val`: Array[Char] = value.toCharArray
    while ((st < len) && ((`val`(st) <= ' ') || trimChars.contains(`val`(st)))) {
      st += 1
    }
    while ((st < len) && ((`val`(len - 1) <= ' ') || trimChars.contains(`val`(len - 1)))) {
      len -= 1
    }
    if ((st > 0) || (len < value.length)) value.substring(st, len) else value
  }

  /**
   * Removes leading and trailing characters that do not match a regular expression
   * @param charRegEx A regular expression that can be matched to a single character (e.g. [A-Z]). The anchors are not needed.
   * @param input Value to apply the regular expressions
   * @return
   */
  def trimNonWhiteListedChars(charRegEx: String)(input: String): String = {
    // TODO This is not very efficient. Think about how to do it better
    val whitelist = ("^(" + charRegEx + ")$").r
    def negativeMatch = { char: Char => !whitelist.pattern.matcher(char.toString).matches }
    input.
      dropWhile(negativeMatch).
      reverse.dropWhile(negativeMatch).
      reverse
  }

  private def transformedStringFormat(transform: String => String): Formatter[String] = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = {
      val value = data.get(key).map(dataValue => transform(dataValue))
      value.toRight(Seq(FormError(key, "error.required", Nil)))
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }
}
