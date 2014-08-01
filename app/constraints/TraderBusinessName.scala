package constraints

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object TraderBusinessName {

  def validTraderBusinessName: Constraint[String] =
    Constraint[String]("constraint.validTraderBusinessName") { restrictedString =>
      val whitelist = """^[a-zA-Z0-9][A-Za-z0-9\s\+\-\(\)\.\&\,\@\']*$""".r
      if (whitelist.pattern.matcher(restrictedString).matches) Valid
      else Invalid(ValidationError("error.validTraderBusinessName"))
    }
}