package uk.gov.dvla.vehicles.presentation.common.views.helpers

import play.api.Play
import play.api.Play.current
import scala.language.implicitConversions

// See the Scala docs for value scala.language.implicitConversions for a discussion why the feature should be explicitly enabled.

object HtmlArgsExtensions {

  final class RichHtmlArgs(htmlArgs: Map[Symbol, Any], validationOff: Boolean = Play.isTest) {
    private final val DefaultMaxLength = 60

    // Always have a maxLength on production, so if you forgot to add one then the default is used.
    // We need to be able to override this behaviour when running integration tests that check that
    // server-side error messages are shown in non-html5 browser
    def maxLengthRules =
      (htmlArgs.contains('maxLength), validationOff) match {
        case (true, true) => htmlArgs - 'maxLength // Remove maxLength when testing so we can have integration tests cause server side validation errors.
        case (true, false) => htmlArgs // No change
        case (false, true) => htmlArgs // No change
        case (false, false) =>
          // On production we should have a maxLength, so if you forgot to add one then the default is used.
          htmlArgs + ('maxLength -> DefaultMaxLength)
      }

    // Always turn off autocomplete to protect user details.
    def autoCompleteRules: Map[Symbol, Any] =
      htmlArgs.get('autocomplete) match {
        case Some(value) =>
          value match {
            case "on" => htmlArgs + ('autocomplete -> "off")
            case "off" => htmlArgs
          }
        case None => htmlArgs + ('autocomplete -> "off")
      }
  }

  implicit def richHtmlArgs(htmlArgs: Map[Symbol, Any]) = new RichHtmlArgs(htmlArgs)
  // TODO can this be replaced with the way we use implicits in the EncryptedCookieImplicits
}