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
      ("AB1C2DE", "AB1C 2DE"),
      ("A 099AA", "A9 9AA" ),
      ("A09A9AA", "A9A 9AA" ),
      ("A999AA", "A99 9AA" ),
      ("AA099AA", "AA9 9AA" ),
      ("AA09A9AA", "AA9A 9AA" ),
      ("AA999AA", "AA99 9AA" ),
      ("A 011AA", "A1 1AA" ),
      ("AA1A1AA", "AA1A 1AA" )
    )

    inputs.foreach { case (input, expected) =>
      s"transform '$input' correctly" in {
        Postcode.formatPostcode(input) should equal(expected)
      }
    }
  }
}
