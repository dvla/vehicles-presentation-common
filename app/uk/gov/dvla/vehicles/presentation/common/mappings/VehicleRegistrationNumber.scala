package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views
import uk.gov.dvla.vehicles.presentation.common.views.constraints.RegistrationNumber.validRegistrationNumber
import views.helpers.FormExtensions.nonEmptyTextWithTransform

object VehicleRegistrationNumber {
  final val MinLength = 2
  final val MaxLength = 8

  def registrationNumber: Mapping[String] = {
    def transform(s: String) = s.replace(" ", "").toUpperCase
    nonEmptyTextWithTransform(transform)(MinLength, MaxLength) verifying validRegistrationNumber
  }
}
