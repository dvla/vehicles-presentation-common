package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.model.Address
import AddressPicker.SearchByPostcodeField
import AddressPicker.ShowSearchFields
import AddressPicker.ShowAddressFields
import AddressPicker.AddressLine1Id
import AddressPicker.AddressLine2Id
import AddressPicker.AddressLine3Id
import AddressPicker.PostTownId
import AddressPicker.PostcodeId
import AddressPicker.RememberId
import uk.gov.dvla.vehicles.presentation.common.views.constraints

class AddressPickerSpec extends UnitSpec {
  private val formatter = AddressPicker.formatter()
  private val extraData = Map(
    s"dp.$SearchByPostcodeField" -> "BB22BB",
    s"dp.$ShowSearchFields" -> "true",
    s"dp.$ShowAddressFields" -> "true"
  )
  val fullModel = Address(
    "address line 1",
    Some("address line 2"),
    Some("address line 3"),
    "Post town",
    "N19 3NN",
    remember = true
  )
  val missingOptional = fullModel.copy(
    streetAddress2 = None,
    streetAddress3 = None
  )

  "formatter unbind" should {
    "generate the right data from all fields" in {
      formatter.unbind("datePicker1", fullModel) should equal(Map(
        s"datePicker1.$RememberId" ->  "on",
        s"datePicker1.$AddressLine1Id" -> fullModel.streetAddress1,
        s"datePicker1.$AddressLine2Id" -> fullModel.streetAddress2.get,
        s"datePicker1.$AddressLine3Id" -> fullModel.streetAddress3.get,
        s"datePicker1.$PostTownId" -> fullModel.postTown,
        s"datePicker1.$PostcodeId" -> fullModel.postCode
      ))
    }

    "generate the right data from mandatory fields only" in {
      formatter.unbind("datePicker5", missingOptional) should equal(Map(
        s"datePicker5.$AddressLine1Id" -> fullModel.streetAddress1,
        s"datePicker5.$PostTownId" -> fullModel.postTown,
        s"datePicker5.$PostcodeId" -> fullModel.postCode,
        s"datePicker5.$RememberId" -> "on"
      ))
    }
  }

  "formatter bind" should {
    "bind valid data" in {
      formatter.bind("datePicker1", Map(
        s"datePicker1.$RememberId" ->  "on",
        s"datePicker1.$AddressLine1Id" -> fullModel.streetAddress1,
        s"datePicker1.$AddressLine2Id" -> fullModel.streetAddress2.get,
        s"datePicker1.$AddressLine3Id" -> fullModel.streetAddress3.get,
        s"datePicker1.$PostTownId" -> fullModel.postTown,
        s"datePicker1.$PostcodeId" -> fullModel.postCode
      )) should equal (Right(fullModel))

      formatter.bind("datePicker2", formatter.unbind("datePicker2", fullModel)) should equal(Right(fullModel))
    }

    "bind valid data with optional fields" in {
      formatter.bind("dp2", formatter.unbind("dp2", missingOptional)) should equal(Right(missingOptional))
    }

    "validate required address line 1" in {
      val data = formatter.unbind("dp", fullModel.copy(streetAddress1 = "")) ++ extraData
      formatter.bind("dp", data) should equal(
        Left(Seq(FormError(s"dp.$AddressLine1Id", "error.address.addressLine1")))
      )
    }

    "validate required post town" in {
      val data = formatter.unbind("dp", fullModel.copy(postTown = "")) ++ extraData
      formatter.bind("dp", data) should equal(
        Left(Seq(FormError(s"dp.$PostTownId", "error.address.postTown")))
      )
    }

    "validate required post code" in {
      val data = formatter.unbind("dp", fullModel.copy(postCode = "")) ++ extraData
      formatter.bind("dp", data) should equal(
        Left(Seq(FormError(s"dp.$PostcodeId", "error.address.postCode")))
      )
    }

    "catch illegal characters in address lines and post town" in {
      val illegalChars = Address(
        "address line *&^*&^%$^ 1",
        Some("@#^&*$address line 2"),
        Some("address line 3 |||(*&(*"),
        "Post town(&(*&(",
        "1@#*^",
        remember = true
      )
      val data = formatter.unbind("dp", illegalChars) ++ extraData
      formatter.bind("dp", data) should equal(
        Left(Seq(
          FormError(s"dp.$AddressLine1Id", "error.address.characterInvalid"),
          FormError(s"dp.$AddressLine2Id", "error.address.characterInvalid"),
          FormError(s"dp.$AddressLine3Id", "error.address.characterInvalid"),
          FormError(s"dp.$PostTownId", "error.postTown.characterInvalid"),
          FormError(s"dp.$PostcodeId", "error.restricted.validPostcode", Seq(constraints.Postcode.regex))
        ))
      )
    }

    "validate min length of the post code" in {
      val data = formatter.unbind("dp", fullModel.copy(postCode = "AAA ")) ++ extraData
      formatter.bind("dp", data) match {
        case Left(errors) => errors should contain(FormError(s"dp.$PostcodeId", "error.minLength", Seq(5)))
        case _ => fail("Errors expected at that point")
      }
    }

    "return validation error if postcode is visible and valid and address fields are invisible" in {
      val data = formatter.unbind("dp", fullModel) ++ Map(
        s"dp.$SearchByPostcodeField" -> "BB22BB",
        s"dp.$ShowSearchFields" -> "true",
        s"dp.$ShowAddressFields" -> "false"
      )
      formatter.bind("dp", data) match {
        case Left(errors) => errors should contain(FormError(s"dp.$SearchByPostcodeField", "error.required.address"))
        case _ => fail("Errors expected at that point")
      }
    }
  }
}
