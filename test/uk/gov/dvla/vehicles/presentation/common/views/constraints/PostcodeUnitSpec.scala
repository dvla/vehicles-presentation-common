package uk.gov.dvla.vehicles.presentation.common.views.constraints

import uk.gov.dvla.vehicles.presentation.common.UnitSpec

final class PostcodeUnitSpec extends UnitSpec {

  /**
   * Test postcode formatting
   */
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
  inputs.map(postcodeTuple => s"transform '${postcodeTuple._1}' correctly" in {
    val result = Postcode.formatPostcode(postcodeTuple._1)
    result should equal(postcodeTuple._2)
  })
}
