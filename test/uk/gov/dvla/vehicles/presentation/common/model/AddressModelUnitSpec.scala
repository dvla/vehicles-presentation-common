package uk.gov.dvla.vehicles.presentation.common.model

import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, WithApplication}

class AddressModelUnitSpec extends UnitSpec {

  "from" should {
    "construct an address model from a supplied comma delimited string" in new WithApplication {
      val addressString = "line1, line2, line3, post town, post code"
      val addressModel = AddressModel.from(addressString)
      addressModel.address(0) should equal("line1")
      addressModel.address(1) should equal("line2")
      addressModel.address(2) should equal("line3")
      addressModel.address(3) should equal("post town")
      addressModel.address(4) should equal("post code")
      addressModel.uprn should equal (None)
    }
    "construct an address model from a string containing blank lines commas" in new WithApplication {
      val addressString = "line1,, line3, post town, post code"
      val addressModel = AddressModel.from(addressString)
      addressModel.address(0) should equal("line1")
      addressModel.address(1) should equal("")
      addressModel.address(2) should equal("line3")
      addressModel.address(3) should equal("post town")
      addressModel.address(4) should equal("post code")
      addressModel.uprn should equal (None)
    }
    "construct an address model from a supplied string with no comma" in new WithApplication {
      val addressString = "line1"
      val addressModel = AddressModel.from(addressString)
      addressModel.address(0) should equal("line1")
      addressModel.uprn should equal (None)
    }
  }
  "construct an address model from an empty string" in new WithApplication {
    val addressString = ""
    val addressModel = AddressModel.from(addressString)
    addressModel.address(0) should equal("")
    addressModel.uprn should equal (None)
  }
}