package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.constraints.NumberOnly.rules

final class NumberOnlyUnitSpec extends UnitSpec {

  "rules" should {

    val validNumbers = Seq("1234", "0")
    validNumbers.foreach { validNumber =>
      s"indicate the number is valid: $validNumber" in {
        rules(validNumber) should equal(Valid)
      }
    }

    val invalidNumbers = Seq("", "test", "123,", "-1", "0.1")
    invalidNumbers.foreach { invalidNumber =>
      s"indicate not a valid number: $invalidNumber" in {
        val result = rules(invalidNumber)
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal("error.restricted.validNumberOnly")
      }
    }
  }
}
