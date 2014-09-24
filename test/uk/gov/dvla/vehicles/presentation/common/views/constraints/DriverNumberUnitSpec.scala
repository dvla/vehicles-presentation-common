package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{ValidationError, Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

final class DrivernNumberUnitSpec extends UnitSpec {

  "validDriverNumber" should {

    val validInputs = Seq(
      "ABCDE711215E9FGH",
      "ABCDE711215EF9GH",
      "ABCD9711215EF9GH",
      "ABC99711215EF9GH",
      "AB999711215EF9GH",
      "A9999711215EF9GH",
      "ABCD9761215EF9GH",
      "ABCD9711215EF8GH",
      "ABCD9711215EF7GH",
      "ABCD9711215EF6GH",
      "ABCD9711215EF5GH",
      "ABCD9711215EF4GH",
      "ABCD9711215EF3GH",
      "ABCD9711215EF2GH",
      "ABCD9711215EFXGH",
      "ABCD9711215EFWGH",
      "ABCD9711215EFVGH",
      "ABCD9711215EFUGH",
      "ABCD9711215EFTGH",
      "ABCD9711215EFSGH",
      "ABCD9711215EFRGH",
      "ABCD9711215EFPGH",
      "ABCD9711215EFNGH",
      "ABCD9711215EFMGH",
      "ABCD9711215EFLGH",
      "ABCD9711215EFKGH",
      "ABCD9711215EFJGH",
      "ABCD9711215EFHGH",
      "ABCD9711215EFGGH",
      "ABCD9711215EFFGH",
      "ABCD9711215EFEGH",
      "ABCD9711215EFDGH",
      "ABCD9711215EFCGH",
      "ABCD9711215EFBGH",
      "ABCD9711215EFAGH",
      "ABCD9751215EFAGH"
    )

    val invalidInputs = Seq(
      "ABCDE7112159FIGH",
      "ABCDE711215EFIGH",
      "ABCDE711215EFOGH",
      "ABCDE711215EFQGH",
      "ABCDE711215EFYGH",
      "ABCDE711215EFZGH",
      "ABCDE711215EF0GH",
      "ABCDE711215EF1GH",
      "9ABCD711215EF9GH",
      "99ABC711215EF9GH",
      "999AB711215EF9GH",
      "9999A711215EF9GH",
      "99999711215EF9GH",
      "A711215EF9GH",
      "AB711215EF9GH",
      "ABC711215EF9GH",
      "ABCD711215EF9GH",
      "ABCDEA11215E9FGH",
      "ABCDE700215E9FGH",
      "ABCDE713215E9FGH",
      "ABCDE750215E9FGH",
      "ABCDE763215E9FGH",
      "ABCDE7AA215E9FGH",
      "ABCDE711005E9FGH",
      "ABCDE711325E9FGH",
      "ABCDE711AA5E9FGH",
      "ABCDE71121AE9FGH"
    )

    validInputs.foreach { case (input) =>
      s"transform '$input' correctly" in {
        DriverNumber.validDriverNumber(input) should equal(Valid)
      }
    }

    invalidInputs.foreach { case (input) =>
      s"fail '$input'" in {
        DriverNumber.validDriverNumber(input) should equal(Invalid(ValidationError("error.driverNumber")))
      }
    }

  }

 }
