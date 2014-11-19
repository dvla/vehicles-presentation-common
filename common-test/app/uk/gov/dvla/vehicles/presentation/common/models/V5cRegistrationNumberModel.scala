package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Mapping
import play.api.data.Forms.nonEmptyText
import uk.gov.dvla.vehicles.presentation.common.views.constraints.RegistrationNumber.validRegistrationNumber

case class V5cRegistrationNumberModel(v5cRegistrationNumber: String)

object V5cRegistrationNumberModel {

  object Form {
    val v5cRegistrationNumberID = "v5cRegistrationNumber"
    val v5cRegistrationNumberValid = "A2"
    val maxLength = 7
    val key = "V5cRegistrationNumber"

    def V5CRegistrationNumber (minLength: Int = Int.MinValue, maxLength: Int = Int.MaxValue): Mapping[String] = {
      nonEmptyText(minLength, maxLength) verifying validRegistrationNumber
    }
  }
}
