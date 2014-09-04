package models

import play.api.data.Mapping
import play.api.data.Forms._
import play.api.data.validation.{ValidationError, Invalid, Valid, Constraint}

case class PostcodeModel(postcode:String)

object PostcodeModel {

  object Form {
    final val PostcodeId = "Postcode"
    final val MinLength = 5
    final val MaxLength = 8

    final val Mapping =  mapping(
      PostcodeId -> postcode()
    )(PostcodeModel.apply)(PostcodeModel.unapply)

    def postcode (minLength: Int = MinLength, maxLength: Int = MaxLength): Mapping[String] = {
      nonEmptyText(minLength, maxLength) verifying validPostcode
    }

    def validPostcode: Constraint[String] = Constraint[String]("constraint.restrictedvalidPostcode") { input =>
      val inputRegex = """^(?i)(GIR 0AA)|((([A-Z][0-9][0-9]?)|(([A-Z][A-HJ-Y][0-9][0-9]?)|(([A-Z][0-9][A-Z])|([A-Z][A-HJ-Y][0-9]?[A-Z]))))[ ]?[0-9][A-Z]{2})$""".r
      inputRegex.pattern.matcher(input).matches match {
        case true => Valid
        case false => Invalid(ValidationError("error.restricted.validPostcode"))
      }
    }
  }
}
