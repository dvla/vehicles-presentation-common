package uk.gov.dvla.vehicles.presentation.common.views.helpers

import scala.language.implicitConversions

// See the Scala docs for value scala.language.implicitConversions for a discussion why the feature should be explicitly enabled.

object HtmlArgsExtensions {

  implicit class RichHtmlArgs(val htmlArgs: Map[Symbol, Any]) extends AnyVal {

    // Always have a maxLength on production, so if you forgot to add one then the default is used. We need to be able
    // to override this behaviour when running integration tests that check that server-side error messages are shown
    // in non-html5 browser
    def withMaxLength = {
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
        val key = Symbol("aria-describedby")
        htmlArgs + (key -> s"$idOfRelatedField-hint")
      }
      else htmlArgs

    def withTypeAttribute: Map[Symbol, Any] =
      if (htmlArgs.contains('typeTel)) htmlArgs - 'typeTel + ('type -> """tel onkeypress="check(event, this);"""")
      else if (htmlArgs.contains('typeFleetNumber)) htmlArgs - 'typeFleetNumber + ('type -> """tel onkeyup="this.value=this.value.replace(/[^\d/-]/g,'')" onkeydown="this.value=this.value.replace(/[^\d/-]/g,'')"""")
      else if (htmlArgs.contains('typeEmail)) htmlArgs - 'typeFleetNumber + ('type -> "email")
      else if (htmlArgs.contains('alphabeticalOnly)) htmlArgs - 'alphabeticalOnly + ('type -> """text onkeyup="this.value=this.value.replace(/[^a-zA-Z]/g,'')" onkeydown="this.value=this.value.replace(/[^a-zA-Z]/g,'')"""")
      else htmlArgs + ('type -> "text")
  }

}