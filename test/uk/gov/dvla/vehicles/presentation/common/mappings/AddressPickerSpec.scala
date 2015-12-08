package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.model.{SearchFields, Address}
import AddressPicker.SearchByPostcodeField
import AddressPicker.ShowSearchFields
import AddressPicker.ShowAddressSelect
import AddressPicker.ShowAddressFields
import AddressPicker.AddressListSelect
import AddressPicker.AddressLine1Id
import AddressPicker.AddressLine2Id
import AddressPicker.AddressLine3Id
import AddressPicker.PostTownId
import AddressPicker.PostcodeId
import AddressPicker.RememberId
import uk.gov.dvla.vehicles.presentation.common.views.constraints

class AddressPickerSpec extends UnitSpec {
  private val formatter = AddressPicker.formatter()
  val searchFields = SearchFields(true, true, true, Some("BB22BB"), Some("2"), remember = true)
  val fullModel = Address(
    searchFields,
    "address line 1",
    Some("address line 2"),
    Some("address line 3"),
    "Post town",
    "N19 3NN"
  )
  val missingOptional = fullModel.copy(
    searchFields = SearchFields(true, true, true, Some("NN33NN"), None, remember = false),
    streetAddress2 = None,
    streetAddress3 = None
  )


  "formatter unbind" should {
    "generate the right data from all fields" in {
      formatter.unbind("datePicker1", fullModel) should equal(Map(
        s"datePicker1.$SearchByPostcodeField" -> searchFields.postCode.get,
        s"datePicker1.$ShowSearchFields" -> searchFields.showSearchFields.toString,
        s"datePicker1.$ShowAddressSelect" -> searchFields.showAddressSelect.toString,
        s"datePicker1.$ShowAddressFields" -> searchFields.showAddressFields.toString,
        s"datePicker1.$AddressListSelect" -> searchFields.listOption.get.toString,
        s"datePicker1.$RememberId" ->  "true",
        s"datePicker1.$AddressLine1Id" -> fullModel.streetAddress1,
        s"datePicker1.$AddressLine2Id" -> fullModel.streetAddress2.get,
        s"datePicker1.$AddressLine3Id" -> fullModel.streetAddress3.get,
        s"datePicker1.$PostTownId" -> fullModel.postTown,
        s"datePicker1.$PostcodeId" -> fullModel.postCode
      ))
    }

    "generate the right data from mandatory fields only" in {
      formatter.unbind("datePicker5", missingOptional) should equal(Map(
        s"datePicker5.$SearchByPostcodeField" -> missingOptional.searchFields.postCode.get,
        s"datePicker5.$ShowSearchFields" -> missingOptional.searchFields.showSearchFields.toString,
        s"datePicker5.$ShowAddressSelect" -> missingOptional.searchFields.showAddressSelect.toString,
        s"datePicker5.$ShowAddressFields" -> missingOptional.searchFields.showAddressFields.toString,
        s"datePicker5.$AddressLine1Id" -> fullModel.streetAddress1,
        s"datePicker5.$PostTownId" -> fullModel.postTown,
        s"datePicker5.$PostcodeId" -> fullModel.postCode
      ))
    }
  }

  "formatter bind" should {
    "bind valid data" in {
      formatter.bind("datePicker1", Map(
        s"datePicker1.$SearchByPostcodeField" -> searchFields.postCode.get,
        s"datePicker1.$ShowSearchFields" -> searchFields.showSearchFields.toString,
        s"datePicker1.$ShowAddressSelect" -> searchFields.showAddressSelect.toString,
        s"datePicker1.$ShowAddressFields" -> searchFields.showAddressFields.toString,
        s"datePicker1.$AddressListSelect" -> searchFields.listOption.get.toString,
        s"datePicker1.$RememberId" ->  "true",
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
      formatter.bind("dp", formatter.unbind("dp", fullModel.copy(streetAddress1 = ""))) should equal(
        Left(Seq(FormError(s"dp.$AddressLine1Id", "error.address.addressLine1")))
      )
    }

    "validate required post town" in {
      formatter.bind("dp", formatter.unbind("dp", fullModel.copy(postTown = ""))) should equal(
        Left(Seq(FormError(s"dp.$PostTownId", "error.address.postTown")))
      )
    }

    "validate required post code" in {
      formatter.bind("dp", formatter.unbind("dp", fullModel.copy(postCode = ""))) should equal(
        Left(Seq(FormError(s"dp.$PostcodeId", "error.address.postCode")))
      )
    }

    "catch illegal characters in address lines and post town" in {
      val illegalChars = Address(
        searchFields,
        "address line *&^*&^%$^ 1",
        Some("@#^&*$address line 2"),
        Some("address line 3 |||(*&(*"),
        "Post town(&(*&(",
        "1@#*^"
      )
      formatter.bind("dp", formatter.unbind("dp", illegalChars)) should equal(
        Left(Seq(
          FormError(s"dp.$AddressLine1Id", "error.address.characterInvalid"),
          FormError(s"dp.$AddressLine2Id", "error.address.characterInvalid"),
          FormError(s"dp.$AddressLine3Id", "error.address.characterInvalid"),
          FormError(s"dp.$PostTownId", "error.address.postTown.characterInvalid"),
          FormError(s"dp.$PostcodeId", "error.restricted.validPostcode", Seq(constraints.Postcode.regex))
        ))
      )
    }

    "validate min length of the post code" in {
      formatter.bind("dp", formatter.unbind("dp", fullModel.copy(postCode = "AAA "))) match {
        case Left(errors) => errors should contain(FormError(s"dp.$PostcodeId", "error.minLength", Seq(5)))
        case _ => fail("Errors expected at that point")
      }
    }

    "return validation error if postcode is visible and valid and address fields are invisible" in {
      val searchFields = SearchFields(true, false, false, Some("BB22BB"), None, remember = false)
      formatter.bind("dp", formatter.unbind("dp", fullModel.copy(searchFields = searchFields))) match {
        case Left(errors) => errors should contain(FormError(s"dp.$SearchByPostcodeField", "error.required.address"))
        case _ => fail("Errors expected at that point")
      }
    }
  }
}