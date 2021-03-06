package uk.gov.dvla.vehicles.presentation.common.model

import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, TestWithApplication}

class AddressModelUnitSpec extends UnitSpec {

  "from" should {
    "construct an address model from a supplied comma delimited string" in new TestWithApplication {
      val addressString = "line1, line2, line3, post town, post code"
      val addressModel = AddressModel.from(addressString)
      addressModel.address(0) should equal("line1")
      addressModel.address(1) should equal("line2")
      addressModel.address(2) should equal("line3")
      addressModel.address(3) should equal("post town")
      addressModel.address(4) should equal("post code")
    }
    "construct an address model from a string containing blank lines commas" in new TestWithApplication {
      val addressString = "line1,, line3, post town, post code"
      val addressModel = AddressModel.from(addressString)
      addressModel.address(0) should equal("line1")
      addressModel.address(1) should equal("")
      addressModel.address(2) should equal("line3")
      addressModel.address(3) should equal("post town")
      addressModel.address(4) should equal("post code")
    }
    "construct an address model from a supplied string with no comma" in new TestWithApplication {
      val addressString = "line1"
      val addressModel = AddressModel.from(addressString)
      addressModel.address(0) should equal("line1")
    }
  }
  "construct an address model from an empty string" in new TestWithApplication {
    val addressString = ""
    val addressModel = AddressModel.from(addressString)
    addressModel.address(0) should equal("")
  }
}