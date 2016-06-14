package uk.gov.dvla.vehicles.presentation.common.model

import uk.gov.dvla.vehicles.presentation.common.helpers.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.models.{AddressLinesViewModel, AddressAndPostcodeViewModel}

class VmAddressModelSpec extends UnitSpec {
  val testAddress = List("THIS STREET", "MYTOWN")
  val postCode = "AA1 1AA"
  val testAddressWithPostCode = testAddress :+ postCode

  private def vmAddressModelWithString(firstLine: String): String =
    VmAddressModel.from(firstLine + "," + testAddressWithPostCode.mkString(",")).address(0)

  private def vmAddresssModelWithAddressAndPostcodeViewModel(firstLine: String): String =
    VmAddressModel.from(AddressAndPostcodeViewModel(AddressLinesViewModel(buildingNameOrNumber = firstLine,
        line2 = Option(testAddress(0)),
        postTown =  testAddress(1)
      ), postCode
    )).address(0)

  "VmAddressModel.from(String) " should {
    "join address lines when first line is 4/17 (< 4 alpha chars)" in {
      vmAddressModelWithString("4/17") should
        equal("4/17 " + testAddress(0))
    }
    "join address lines when first line is 123 (< 4 alpha chars)" in {
      vmAddressModelWithString("123") should
        equal("123 " + testAddress(0))
    }
    "join address lines when first line is abc123 (< 4 alpha chars)" in {
      vmAddressModelWithString("abc123") should
        equal("abc123 " + testAddress(0))
    }
  }
  "VmAddressModel.from(address: AddressAndPostcodeViewModel, postcode: String) " should {
    "join address lines with 4/17, 123 this street (< 4 alpha chars)" in {
      vmAddresssModelWithAddressAndPostcodeViewModel("4/17") should
        equal("4/17 " + testAddress(0))
    }
    "join address lines when first line is 123 (< 4 alpha chars)" in {
      vmAddresssModelWithAddressAndPostcodeViewModel("123") should
        equal("123 " + testAddress(0))
    }
    "join address lines when first line is abc123 (< 4 alpha chars)" in {
      vmAddresssModelWithAddressAndPostcodeViewModel("ABC123") should
        equal("ABC123 " + testAddress(0))
    }
  }
  "VmAddressModel " should {
    "leave address lines, if first line has 4 alpha characters" in {
      vmAddressModelWithString("abcd1234") should equal("abcd1234")
    }
  }
}
