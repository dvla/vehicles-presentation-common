package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class BusinessNameUnitSpec extends UnitSpec {

  "validBusinessName" should {

    BusinessNameUnitSpec.validNames.foreach { name =>
      s"indicate the business name is valid: $name" in {
        BusinessName.validBusinessName(name) should equal(Valid)
      }
    }

    BusinessNameUnitSpec.invalidNames.foreach { name =>
      s"indicate the business name is invalid: $name" in {
        val result = BusinessName.validBusinessName(name)
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal(("error.validBusinessName"))
      }
    }
  }
}

object BusinessNameUnitSpec {
  val validNames = Seq("Bob Jones", "Fred's Cars", "Baby-blue Cars", "Bob, Fred and Kerry's Cars", "A", "3M's Cars",
    "M&S", "Bob/Kerry's Cars", "Bob (bob) Cars", "Bob.P Cars", "abcdefghij" * 3, "qw", "12", "q-",
    "q,", "q'", "q&", "a()", "a(()())", "Bob (& Son)")


  // The following are explicitly invalid: +, @
  val invalidNames = Seq("Bob + Kerry's Cars", "Bob@Bob Cars", "(Keri's Motors)", "&& Keri's Motors&&",
    "", "'-", "..", "w*", "q+", "q!", "q£", "q$", "q%", "q^", "£a", "%a", "(Bob (& Son)")


}