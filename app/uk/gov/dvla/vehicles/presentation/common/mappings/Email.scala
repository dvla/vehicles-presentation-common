package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.format.Formats.stringFormat
import play.api.data.Forms.of
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Email.{ptr, emailStyleValid}

object Email {
  final val TraderEmailMinLength = 3
  final val TraderEmailMaxLength = 254
  final val EmailUsernameMaxLength = 64
  final val EmailDomainSectionMaxLength = 63
  final val InvalidUsernameChar = "\"."
  final val InvalidDomainStartEndChar = "-"
  final val InvalidDomainContentChar = "/"

  def email: Mapping[String] = of[String] verifying emailAddress

  def emailAddress: Constraint[String] = Constraint[String]("constraint.email") {
    e =>
      if (!(TraderEmailMinLength to TraderEmailMaxLength contains e.length)) Invalid(ValidationError("error.email"))
      else if (ptr.matcher(e).matches())
        if (emailStyleValid(e)) Valid else Invalid(ValidationError("error.email"))
      else Invalid(ValidationError("error.email"))
  }
}