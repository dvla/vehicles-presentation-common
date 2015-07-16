package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraint

object BusinessKeeperName {
  def validBusinessKeeperName: Constraint[String] = BusinessName.nameConstrain(
    "constraint.validBusinessKeeperName",
    "error.validBusinessName",
    "error.invalidBraces",
    "error.oddNumberOfQuotes"
  )
}
