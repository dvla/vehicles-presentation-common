package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class BusinessKeeperNameUnitSpec extends UnitSpec {
  val invalidNameWithBraces = Seq("a(", "a)(", "a(a))", "a(a(b)")

  val invalidNameWithQuotes = Seq("a single quote\"", "triple \"quote\" st\"ring")

  "validBusinessKeeperName" should {

    BusinessNameUnitSpec.validNames.foreach { name =>
      s"indicate the business keeper name is valid: $name" in {
        BusinessKeeperName.validBusinessKeeperName(name) should equal(Valid)
      }
    }

    BusinessNameUnitSpec.invalidNames.foreach { name =>
      s"indicate the business keeper name is invalid: $name" in {
        val result = BusinessKeeperName.validBusinessKeeperName(name)
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal("error.validBusinessKeeperName")
      }
    }

    invalidNameWithBraces.foreach { name =>
      s"indicate the business name has invalid braces: $name" in {
        val result = BusinessKeeperName.validBusinessKeeperName(name)
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal(("error.invalidBracesOrQuotes"))
      }
    }

    invalidNameWithQuotes.foreach { name =>
      s"indicate the business name has invalid braces: $name" in {
        val result = BusinessKeeperName.validBusinessKeeperName(name)
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal(("error.invalidBracesOrQuotes"))
      }
    }
  }

}
