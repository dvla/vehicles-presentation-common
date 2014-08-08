package uk.gov.dvla.vehicles.presentation.common

object LogFormats {
  private final val anonymousChar = "*"

  def anonymize(input: String): String = {
    val startOfNonAnonymizedText =
      if (input.length == 0) 0
      else if (input.length > 8) 4
      else input.length / 2
    anonymousChar * (input.length - startOfNonAnonymizedText) + input.takeRight(startOfNonAnonymizedText)
  }
}