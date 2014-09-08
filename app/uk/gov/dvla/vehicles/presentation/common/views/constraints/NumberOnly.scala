package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern

object NumberOnly {

  def rules: Constraint[String] = pattern(
    regex = """^\d[0-9]*$""".r,
    name = "constraint.restricted.validNumberOnly",
    error = "error.restricted.validNumberOnly"
  )
}