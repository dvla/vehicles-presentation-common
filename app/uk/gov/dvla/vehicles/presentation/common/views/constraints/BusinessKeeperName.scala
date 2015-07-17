package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Invalid, ValidationError, Valid, Constraint}

object BusinessKeeperName {
  def validBusinessKeeperName: Constraint[String] = BusinessName.nameConstrain(
    "constraint.validBusinessKeeperName",
    "error.validBusinessName",
    "error.invalidBraces",
    "error.oddNumberOfQuotes"
  )

  def atLeastACharacter: Constraint[String] = Constraint[String]("constraint.atLeastOneChar") {
    case str: String =>
      if (str.replaceAll( """[^A-Za-z]""", "").length < 1)
        Invalid(ValidationError("error.atLeastOneChar"))
      else
        Valid
  }
}
