package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object DropDown {

  def validDropDown(dropDownOptions: Map[String, String]): Constraint[String] =
    Constraint("constraint.validDropDown") { input =>
      if (dropDownOptions.contains(input)) Valid
      else Invalid(ValidationError("error.dropDownInvalid"))
    }
}