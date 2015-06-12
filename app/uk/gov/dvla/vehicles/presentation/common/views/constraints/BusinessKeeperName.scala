package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Valid, ValidationError, Invalid, Constraint}
import play.api.data.validation.Constraints.pattern
import BusinessName.{Pattern, OneCharPattern}

object BusinessKeeperName {

  def validBusinessKeeperName: Constraint[String] = pattern(
    regex = Pattern.r,
    name = "constraint.validBusinessKeeperName",
    error = "error.validBusinessKeeperName")

  def atLeastACharacter: Constraint[String] = Constraint[String]("constraint.atLeastOneChar") {
    case str: String =>
      if (str.replaceAll( """[^A-Za-z]""", "").length < 1)
        Invalid(ValidationError("error.atLeastOneChar"))
      else
        Valid
  }
}
