package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common
import common.views.helpers.FormExtensions.nonEmptyTextWithTransform
import common.views.constraints.BusinessName.validBusinessName

object BusinessName {
  final val MinLength = 2
  final val MaxLength = 56

  def businessNameMapping: Mapping[String] =
    nonEmptyTextWithTransform(_.trim)(MinLength, MaxLength) verifying validBusinessName
}
