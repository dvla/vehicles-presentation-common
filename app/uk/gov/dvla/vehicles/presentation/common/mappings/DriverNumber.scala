package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.format.Formats.stringFormat
import play.api.data.Forms.of
import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views.constraints.DriverNumber.validDriverNumber

object DriverNumber {
  final val MinLength = 1
  final val MaxLength = 16

  def driverNumber: Mapping[String] = of[String] verifying validDriverNumber

}
