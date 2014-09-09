package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.constraints.NumberOnly.rules

final class NumberOnlyUnitSpec extends UnitSpec {

  /**
   * Test valid number formats
   */
  val validNumbers = Seq("1234")
  validNumbers.map(validNumber => s"indicate the number is valid: $validNumber" in {
    val result = rules(validNumber)
    result should equal(Valid)
  })

  /**
   * Test invalid numbers
   */
  val invalidNumbers = Seq("", "test", "123,")
  invalidNumbers.map(invalidNumber => s"indicate not a valid number: $invalidNumber" in {
    val result = rules(invalidNumber)
    val invalid = result.asInstanceOf[Invalid]
    invalid.errors(0).message should equal("error.restricted.validNumberOnly")
  })
}
