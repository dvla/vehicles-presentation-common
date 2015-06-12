package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern

object BusinessName {

  final val Pattern = """^[a-zA-Z0-9][a-zA-Z0-9\s\-\'\,\&\/\(\)\.]*$"""
  final val OneCharPattern = """[a-zA-Z]{1,}"""

  def validBusinessName: Constraint[String] = pattern(
    regex = Pattern.r,
    name = "constraint.validBusinessName",
    error = "error.validBusinessName")
}
