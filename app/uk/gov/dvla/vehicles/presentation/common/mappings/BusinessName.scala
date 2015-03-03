package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common
import common.views.constraints.BusinessName.validBusinessName
import common.views.helpers.FormExtensions.nonEmptyTextWithTransform

object BusinessName {
  final val MinLength = 2
  final val MaxLength = 58

  def businessNameMapping: Mapping[String] =
    nonEmptyTextWithTransform(_.toUpperCase.trim)(MinLength, MaxLength) verifying validBusinessName
}
