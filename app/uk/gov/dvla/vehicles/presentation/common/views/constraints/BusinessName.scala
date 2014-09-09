package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern

object BusinessName {

  def validBusinessName: Constraint[String] = pattern(
    regex = """^[a-zA-Z0-9][a-zA-Z0-9\s\-\'\,]*$""".r,
    name = "constraint.validBusinessName",
    error = "error.validBusinessName")
}
