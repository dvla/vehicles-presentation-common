package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object BusinessName {

  def validBusinessName: Constraint[String] = Constraint[String]("constraint.validBusinessName") { restrictedString =>
      // Allowable characters are a-z, A-Z, 0-9,  -, ‘ and ,
      val whitelist = """^[a-zA-Z0-9\s\-\'\,]{2,}$""".r
      if (whitelist.pattern.matcher(restrictedString).matches) Valid
      else Invalid(ValidationError("error.validBusinessName"))
    }
}
