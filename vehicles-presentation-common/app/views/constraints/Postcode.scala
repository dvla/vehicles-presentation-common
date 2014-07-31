package views.constraints

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object Postcode {

  def validPostcode: Constraint[String] = Constraint("constraint.restricted.validPostcode") { input =>
    val whitelist =
      """^
        |(?i)(GIR 0AA)|
        |((([A-Z][0-9][0-9]?)|
        |(([A-Z][A-HJ-Y][0-9][0-9]?)|
        |(([A-Z][0-9][A-Z])|
        |([A-Z][A-HJ-Y][0-9]?[A-Z]))))[ ]?[0-9][A-Z]{2})
        |$""".stripMargin.replace("\n", "").r

    if (whitelist.pattern.matcher(input).matches) Valid
    else Invalid(ValidationError("error.restricted.validPostcode"))
  }
}