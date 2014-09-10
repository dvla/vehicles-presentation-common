package uk.gov.dvla.vehicles.presentation.common.views.constraints

import uk.gov.dvla.vehicles.presentation.common.UnitSpec

final class RegistrationNumberUnitSpec extends UnitSpec {

  "formatVrm" should {

    val inputs = Seq(
      ("AB12CDE", "AB12 CDE"),
      ("A1", "A 1     "),
      ("A12", "A 12    "),
      ("A123", "A 123   "),
      ("A1234", "A 1234  "),
      ("AB1", "AB 1    "),
      ("AB12", "AB 12   "),
      ("AB123", "AB 123  "),
      ("AB1234", "AB 1234 "),
      ("ABC1", "ABC 1   "),
      ("ABC12", "ABC 12  "),
      ("ABC123", "ABC 123 "),
      ("ABC1234", "ABC 1234"),
      ("ABC1D", "ABC 1D  "),
      ("ABC12D", "ABC 12D "),
      ("ABC123D", "ABC 123D"),
      ("1A", "1 A     "),
      ("0001  A", "1 A     "),
      ("1AB", "1 AB    "),
      ("0001 AB", "1 AB    "),
      ("1ABC", "1 ABC   "),
      ("0001ABC", "1 ABC   "),
      ("12A", "12 A    "),
      ("0012  A", "12 A    "),
      ("12AB", "12 AB   "),
      ("0012 AB", "12 AB   "),
      ("12ABC", "12 ABC  "),
      ("0012ABC", "12 ABC  "),
      ("123A", "123 A   "),
      ("0123  A", "123 A   "),
      ("123AB", "123 AB  "),
      ("0123 AB", "123 AB  "),
      ("123ABC", "123 ABC "),
      ("0123ABC", "123 ABC "),
      ("1234A", "1234 A  "),
      ("1234  A", "1234 A  "),
      ("1234AB", "1234 AB "),
      ("1234 AB", "1234 AB "),
      ("Y9ABC", "Y9 ABC  "),
      ("Y  9ABC", "Y9 ABC  "),
      ("Y12ABC", "Y12 ABC "),
      ("Y 12ABC", "Y12 ABC "),
      ("Y123ABC", "Y123 ABC")
    )

    inputs.foreach { case (input, expected) =>
      s"transform '$input' correctly" in {
        RegistrationNumber.formatVrm(input) should equal(expected)
      }
    }
  }
 }
