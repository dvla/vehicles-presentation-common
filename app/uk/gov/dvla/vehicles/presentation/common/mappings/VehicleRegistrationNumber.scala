package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.nonEmptyText
import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views.constraints.RegistrationNumber
import uk.gov.dvla.vehicles.presentation.common.views.constraints.RegistrationNumber.validRegistrationNumber

object VehicleRegistrationNumber {
  final val MinLength = 2
  final val MaxLength = 8

  def registrationNumber: Mapping[String] = nonEmptyText(MinLength, MaxLength) verifying validRegistrationNumber
}