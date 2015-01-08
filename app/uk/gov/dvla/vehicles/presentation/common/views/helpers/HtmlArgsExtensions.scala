package uk.gov.dvla.vehicles.presentation.common.views.helpers

import scala.language.implicitConversions

// See the Scala docs for value scala.language.implicitConversions for a discussion why the feature should be explicitly enabled.

object HtmlArgsExtensions {

  implicit class RichHtmlArgs(val htmlArgs: Map[Symbol, Any]) extends AnyVal {

    // Always have a maxLength on production, so if you forgot to add one then the default is used.
    // We need to be able to override this behaviour when running integration tests that check that
    // server-side error messages are shown in non-html5 browser
    def withMaxLength = {
      val DefaultMaxLength = 60
      if (htmlArgs.contains('maxLength)) htmlArgs // No change
      else htmlArgs + ('maxLength -> DefaultMaxLength) // On production we should have a maxLength, so if you forgot to add one then the default is used.
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
  }

}