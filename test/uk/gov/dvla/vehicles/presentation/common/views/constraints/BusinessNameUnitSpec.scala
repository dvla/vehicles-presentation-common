package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class BusinessNameUnitSpec extends UnitSpec {

  "validBusinessName" should {

    val validNames = Seq("Bob Jones", "Fred's Cars", "Baby-blue Cars", "Bob, Fred and Kerry's Cars", "A", "3M's Cars")

    validNames.foreach { name =>
      s"indicate the business name is valid: $name" in {
        BusinessName.validBusinessName(name) should equal(Valid)
      }
    }

    // The following are explicitly invalid: +, @, &, (), /
    val invalidNames = Seq("Bob + Kerry's Cars", "Bob@Bob Cars", "Bob & Kerry's Cars", "Bob (bob) Cars",
      "Bob/Kerry's Cars", "", "'-")

    invalidNames.foreach { name =>
      s"indicate the business name is invalid: $name" in {
        val result = BusinessName.validBusinessName(name)
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal("error.validBusinessName")
      }
    }
  }
}
