package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views
import views.constraints.Postcode.validPostcode
import views.helpers.FormExtensions.nonEmptyTextWithTransform

object Postcode {
  private final val MinLength = 5
  final val MaxLength = 8

  def postcode: Mapping[String] = {
    nonEmptyTextWithTransform(_.toUpperCase.trim)(MinLength, MaxLength) verifying validPostcode
  }
}
