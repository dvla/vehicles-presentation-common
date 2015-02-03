package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern
import BusinessName.Pattern

object BusinessKeeperName {

  def validBusinessKeeperName: Constraint[String] = pattern(
    regex = Pattern.r,
    name = "constraint.validBusinessKeeperName",
    error = "error.validBusinessKeeperName")
}
