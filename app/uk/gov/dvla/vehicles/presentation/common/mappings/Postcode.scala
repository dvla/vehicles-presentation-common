package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Postcode.{validPostcode, validPostcodePR}
import uk.gov.dvla.vehicles.presentation.common.views.helpers.FormExtensions.{nonEmptyTextWithTransform, textWithTransform}

object Postcode {
  private final val MinLength = 5
  final val MaxLength = 8

  def postcode: Mapping[String] = {
    nonEmptyTextWithTransform(_.toUpperCase.trim)(MinLength, MaxLength) verifying validPostcode
  }
}

object PostcodePR {

  def postcode: Mapping[String] = {
    textWithTransform(_.toUpperCase.trim)(maxLength = Postcode.MaxLength) verifying validPostcodePR
  }
}
