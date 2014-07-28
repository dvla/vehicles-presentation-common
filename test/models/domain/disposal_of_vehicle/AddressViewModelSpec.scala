package models.domain.disposal_of_vehicle

import viewmodels.AddressViewModel
import AddressViewModel.JsonFormat
import helpers.UnitSpec
import models.domain.common.{AddressLinesModel, AddressAndPostcodeModel}
import play.api.libs.json.Json
import serviceclients.fakes.FakeAddressLookupService.BuildingNameOrNumberValid
import serviceclients.fakes.FakeAddressLookupService.Line2Valid
import serviceclients.fakes.FakeAddressLookupService.Line3Valid
import serviceclients.fakes.FakeAddressLookupService.PostcodeValid
import serviceclients.fakes.FakeAddressLookupService.PostTownValid
import serviceclients.fakes.FakeVehicleLookupWebService.KeeperUprnValid
import viewmodels.AddressViewModel


final class AddressViewModelSpec extends UnitSpec {
  "from" should {
    "translate correctly" in {
      val addressAndPostcodeModel = AddressAndPostcodeModel(addressLinesModel = AddressLinesModel(
        buildingNameOrNumber = BuildingNameOrNumberValid,
        line2 = Some(Line2Valid),
        line3 = Some(Line3Valid),
        postTown = PostTownValid))

      val result = AddressViewModel.from(addressAndPostcodeModel, PostcodeValid)

      result.uprn should equal(None)
      result.address should equal(Seq(
        BuildingNameOrNumberValid.toUpperCase,
        Line2Valid.toUpperCase,
        Line3Valid.toUpperCase,
        PostTownValid.toUpperCase,
        PostcodeValid.toUpperCase))
    }
  }

  "format" should {
    "serialize to json" in {
      val address = AddressViewModel(
        uprn = Some(KeeperUprnValid),
        address = Seq(BuildingNameOrNumberValid, Line2Valid, Line3Valid, PostTownValid, PostcodeValid))
    }

    "deserialize from json" in {
      val fromJson =  Json.fromJson[AddressViewModel](asJson)
      val expected = AddressViewModel(
        uprn = Some(KeeperUprnValid),
        address = Seq(BuildingNameOrNumberValid, Line2Valid, Line3Valid, PostTownValid, PostcodeValid))

      fromJson.asOpt should equal(Some(expected))
    }
  }

  private val asJson = Json.parse(
    s"""{"uprn":10123456789,"address":["1234","line2 stub","line3 stub","postTown stub","$PostcodeValid"]}""")
}
