package webserviceclients.address_lookup.gds

import helpers.UnitSpec
import webserviceclients.fakes.FakeAddressLookupService.{PostcodeValid, PostcodeValidWithSpace}
import utils.helpers.Config

final class WebServiceImplSpec extends UnitSpec {
  "postcodeWithNoSpaces" should {
    "return the same string if no spaces present" in {
      val result = addressLookupService.postcodeWithNoSpaces(PostcodeValid)

      result should equal(PostcodeValid)
    }

    "remove spaces when present" in {
      val result = addressLookupService.postcodeWithNoSpaces(PostcodeValidWithSpace)

      result should equal(PostcodeValid)
    }
  }

  private val addressLookupService = new webserviceclients.address_lookup.gds.WebServiceImpl(new Config())
}