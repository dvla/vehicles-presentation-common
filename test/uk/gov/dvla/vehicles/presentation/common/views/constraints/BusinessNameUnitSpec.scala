package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class BusinessNameUnitSpec extends UnitSpec {

  /**
   * Test valid business names
   */
  val validNames = Seq("Bob Jones", "Fred's Cars", "Baby-blue Cars", "Bob, Fred and Kerry's Cars", "A", "3M's Cars")
  validNames.map(name => s"indicate the business name is valid: $name" in {
    val result = BusinessName.validBusinessName(name)
    result should equal(Valid)
  })

  /**
   * Test invalid business names
   * The following are explicitly invalid: +, @, &, (), /
   */
  val invalidNames = Seq("Bob + Kerry's Cars", "Bob@Bob Cars", "Bob & Kerry's Cars", "Bob (bob) Cars",
    "Bob/Kerry's Cars", "", "'-")
  invalidNames.map(name => s"indicate the business name is invalid: $name" in {
    val result = BusinessName.validBusinessName(name)
    val invalid = result.asInstanceOf[Invalid]
    invalid.errors(0).message should equal("error.validBusinessName")
  })
}
