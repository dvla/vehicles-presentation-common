package uk.gov.dvla.vehicles.presentation.common.views.constraints

import uk.gov.dvla.vehicles.presentation.common.UnitSpec

final class PostcodeUnitSpec extends UnitSpec {

  "formatPostcode" should {

    val inputs = Seq(
      ("A12BC", "A1 2BC"),
      ("A 012BC", "A1 2BC"),
      ("A123BC", "A12 3BC"),
      ("A 123BC", "A12 3BC"),
      ("A1B2CD", "A1B 2CD"),
      ("A 1B2CD", "A1B 2CD"),
      ("AB12CD", "AB1 2CD"),
      ("AB012CD", "AB1 2CD"),
      ("AB123CD", "AB12 3CD"),
      ("AB1C2DE", "AB1C 2DE")
    )

    inputs.foreach { case (input, expected) =>
      s"transform '$input' correctly" in {
        Postcode.formatPostcode(input) should equal(expected)
      }
    }
  }
}
