package models

import play.api.data.Mapping
import play.api.data.Forms._
import play.api.data.validation.{ValidationError, Invalid, Valid, Constraint}

case class V5cRegistrationNumberModel(v5cRegistrationNumber: String)

object V5cRegistrationNumberModel {

  object Form {
    val v5cRegistrationNumberID = "vehicles_v5cRegistrationNumber"
    val v5cRegistrationNumberValid = "A2"
    val maxLength = 7
    val key = "V5cRegistrationNumber"

    def V5CRegistrationNumber (minLength: Int = Int.MinValue, maxLength: Int = Int.MaxValue): Mapping[String] = {
      nonEmptyText(minLength, maxLength) verifying validVRN
    }

    def validVRN: Constraint[String] = Constraint[String]("constraint.restrictedvalidVRN") { input =>
      val inputRegex = """^(([A-Za-z]{3}[0-9]{1,4})|([A-Za-z][0-9]{1,3}[A-Za-z]{3})|([A-Za-z]{3}[0-9]{1,3}[A-Za-z])|([A-Za-z]{2}[0-9]{2}[A-Za-z]{3})|([A-Za-z]{1,3}[0-9]{1,3})|([0-9]{1,3}[A-Za-z]{1,3})|([0-9]{1,4}[A-Za-z]{1})|([0-9]{1,4}[A-Za-z]{1,3})|([0-9]{1,4}[A-Za-z]{1,3})|([A-Za-z]{1,2}[0-9]{1,4}))*$""".r
      inputRegex.pattern.matcher(input).matches match {
        case true => Valid
        case false => Invalid(ValidationError("error.restricted.validVRNOnly"))
      }
    }
  }

}

