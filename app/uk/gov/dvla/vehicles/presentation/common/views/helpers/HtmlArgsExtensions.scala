package uk.gov.dvla.vehicles.presentation.common.views.helpers

import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required.RequiredField

import scala.language.implicitConversions

// See the Scala docs for value scala.language.implicitConversions for a discussion why the feature should be explicitly enabled.

object HtmlArgsExtensions {

  implicit class RichHtmlArgs(val htmlArgs: Map[Symbol, Any]) extends AnyVal {

    // Always have a maxLength on production, so if you forgot to add one then the default is used. We need to be able
    // to override this behaviour when running integration tests that check that server-side error messages are shown
    // in non-html5 browser
    def withMaxLength: Map[Symbol, Any] = {
      if (htmlArgs.contains('maxLength)) htmlArgs // No change
      else {
        // On production we should have a maxLength, so if you forgot to add one then the default is used.
        val DefaultMaxLength = 60
        htmlArgs + ('maxLength -> DefaultMaxLength)
      }
    }

    // Always turn off autocomplete to protect user details.
    def withoutAutoComplete: Map[Symbol, Any] =
      htmlArgs.get('autocomplete) match {
        case Some(value) =>
          value match {
            case "on" => htmlArgs + ('autocomplete -> "off")
            case "off" => htmlArgs
          }
        case None => htmlArgs + ('autocomplete -> "off")
      }

    // Screen readers do not automatically announce the hint text when focus moves to the field, and so screen reader
    // users may be unaware of it as they tab through the form. The aria-describedby attribute creates an association
    // between the hint and the field. It takes the id of the hint container as a value.
    def withAriaDescribedby(hintText: Option[String], idOfRelatedField: String): Map[Symbol, Any] =
      if (hintText.isDefined) {
        val ariaDescribedKey = Symbol("aria-describedby")
        htmlArgs + (ariaDescribedKey -> s"$idOfRelatedField-hint")
      }
      else htmlArgs

    def withCanTabTo(canTabTo: Boolean = true): Map[Symbol, Any] =
      if (!canTabTo) htmlArgs + (Symbol("tabindex") -> -1) else htmlArgs

    def withAutofocus(autofocus: Boolean = false): Map[Symbol, Any] =
      if (autofocus) htmlArgs + (Symbol("autofocus") -> true) else htmlArgs

    // Detects a type attribute passed in using html args, replaces the arg.
    def withTypeAttribute: Map[Symbol, Any] =
      if (htmlArgs.contains('typeTel)) withTypeAttributeTel
      else if (htmlArgs.contains('typeFleetNumber)) withTypeAttributeFleetNumber
      else if (htmlArgs.contains('typeEmail)) withTypeAttributeEmail
      else if (htmlArgs.contains('alphabeticalOnly)) withTypeAttributeAlphabeticalOnly
      else htmlArgs + ('type -> "text")

    def withTypeAttributeTel: Map[Symbol, Any] =
      htmlArgs - 'typeTel + ('type -> """tel""") + ('onkeyup -> """check(event, this);""") +
        ('onkeydown -> """check(event, this);""")

    def withTypeAttributeFleetNumber: Map[Symbol, Any] =
      htmlArgs - 'typeFleetNumber + ('type -> "tel") +
        ('onkeyup -> """this.value=this.value.replace(/[^\d/-]/g,'')""") +
        ('onkeydown -> """this.value=this.value.replace(/[^\d/-]/g,'')""")

    def withTypeAttributeEmail: Map[Symbol, Any] = htmlArgs - 'typeEmail + ('type -> "email")

    def withTypeAttributeAlphabeticalOnly: Map[Symbol, Any] =
      htmlArgs - 'alphabeticalOnly + ('type -> "text") +
        ('onkeyup -> """this.value=this.value.replace(/[^a-zA-Z]/g,'')""") +
        ('onkeydown -> """this.value=this.value.replace(/[^a-zA-Z]/g,'')""")

    def withTypeAttributeText: Map[Symbol, Any] = htmlArgs + ('type -> "text")

    def withTypeAttributeCheckbox: Map[Symbol, Any] = htmlArgs + ('type -> "checkbox")

    def withTypeAttributeRadio: Map[Symbol, Any] = htmlArgs + ('type -> "radio")

    def withAriaInvalid(hasErrors: Boolean): Map[Symbol, Any] =
      if (hasErrors) {
        val ariaInvalidKey = Symbol("aria-invalid")
        htmlArgs + (ariaInvalidKey -> true)
      }
      else htmlArgs

    def withAriaRequired(constraints: Seq[(String, Seq[Any])]): Map[Symbol, Any] =
      if (constraints.exists({ case (key, _) => key == RequiredField})) {
        val ariaRequiredKey = Symbol("aria-required")
        htmlArgs + (ariaRequiredKey -> true)
      }
      else htmlArgs

    def valueElseTrue: Map[Symbol, Any] =
      if (htmlArgs.contains('value)) htmlArgs
      else htmlArgs + ('value -> true)

    def checkedWhenValueMatches(fieldValue: Option[String]): Map[Symbol, Any] =
      fieldValue match {
        case Some(value) if value == htmlArgs.getOrElse('value, true).toString => htmlArgs + ('checked -> "") // Either there's a fieldValue and it matches or there is no fieldValue so try to match against the default.
        case _ => htmlArgs
      }

    def withoutNoOptionalLabel: Map[Symbol, Any] =
      if (htmlArgs.contains('NO_OPTIONAL_LABEL)) htmlArgs - 'NO_OPTIONAL_LABEL // No change
      else htmlArgs
  }
}