package uk.gov.dvla.vehicles.presentation.common.views.constraints

import java.util.regex.Pattern
import play.api.data.validation.{Valid, ValidationError, Invalid, Constraint}
import uk.gov.dvla.vehicles.presentation.common.mappings.DriverNumber.{MinLength, MaxLength}

object DriverNumber {

  private val validDriverNumberRegex = "[a-zA-Z]{1}[a-zA-Z0-9]{4}\\d([05][1-9]|[16][012])(0[1-9]|[12][0-9]|3[01])" +
    "\\d[a-zA-Z]{1}[a-zA-Z9]{1}[98765432xwvutsrpnmlkjhgfedcbaXWVUTSRPNMLKJHGFEDCBA]{1}[a-zA-Z]{2}"

  val ptr = Pattern.compile(validDriverNumberRegex)

  def validDriverNumber: Constraint[String] = Constraint[String]("constraint.driverNumber") {
    dn =>
      if (!(MinLength to MaxLength contains dn.length)) Invalid(ValidationError("error.driverNumber"))
      else if (ptr.matcher(dn).matches()) Valid
      else Invalid(ValidationError("error.driverNumber"))
  }
}
