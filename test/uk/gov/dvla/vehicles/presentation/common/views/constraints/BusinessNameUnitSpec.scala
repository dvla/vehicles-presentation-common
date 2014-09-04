package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{ValidationError, Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class BusinessNameUnitSpec extends UnitSpec {

  /**
   * Test valid business names
   */
  val validNames = Seq("Bob Jones", "Fred's Cars", "Baby-blue Cars", "Bob, Fred and Kerry's Cars", "Aa")
  validNames.map(name => "indicate the business name is valid: " + name in {
    val result = BusinessName.validBusinessName(name)
    result should equal(Valid)
  })

  /**
   * Test invalid business names
   * The following are explicitly invalid: +, @, &, (), /
   */
  val invalidNames = Seq("Bob + Kerry's Cars", "Bob@Bob Cars", "Bob & Kerry's Cars", "Bob (bob) Cars",
    "Bob/Kerry's Cars", "A")
  invalidNames.map(name => "indicate the business name is invalid: " + name in {
    val result = BusinessName.validBusinessName(name)
    result should equal(Invalid(ValidationError("error.validBusinessName")))
  })
}
