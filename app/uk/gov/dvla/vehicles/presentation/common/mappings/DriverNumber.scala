package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views.constraints.DriverNumber.validDriverNumber
import uk.gov.dvla.vehicles.presentation.common.views.helpers.FormExtensions.nonEmptyTextWithTransform

object DriverNumber {
  final val MinLength = 1
  final val MaxLength = 16

  def driverNumber: Mapping[String] =
    nonEmptyTextWithTransform(_.toUpperCase)(MinLength, MaxLength) verifying validDriverNumber
}