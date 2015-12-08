package uk.gov.dvla.vehicles.presentation.common.views.models

import play.api.data.Forms.{mapping, number, optional}
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.mappings.Postcode.postcode
import common.views.models.AddressLinesViewModel.Form.{AddressLinesId, mapping => addressLinesMapping}
import common.views.constraints.BusinessName
import common.views.constraints.Required.RequiredField

case class AddressAndPostcodeViewModel(uprn: Option[Int] = None,
                                       addressLinesModel: AddressLinesViewModel,
                                       postCode: String) {
  def toViewFormat: Seq[String] = addressLinesModel.toViewFormat :+ postCode
}

object AddressAndPostcodeViewModel {
  implicit val AddressAndPostcodeModelFormat = Json.format[AddressAndPostcodeViewModel]
  final val AddressAndPostcodeCacheKey = "addressAndPostcodeCacheKey"
  implicit val Key = CacheKey[AddressAndPostcodeViewModel](AddressAndPostcodeCacheKey)

  object Form {
    final val PostcodeId = "postcode"
    final val UprnId = "uprn"
    //final val MaxLengthOfLinesConcatenated = 120
    // NOTE: total address line max is actually defined by line length x number of address lines i.e. 30 x 3 = 90

    // First line must contain at least 3 alpha characters
    final val buildingNameOrNumberFormat = """([^A-Za-z]*?[A-Za-z][^A-Za-z]*?){3,}""".r

    // Post town cannot contain numbers, can also include punctuation.
    final val postTownFormat = """^[a-zA-Z][A-Za-z\s\-\,\.\/\\]*$""".r

    // Regex states string must contain at least one number or letter, can also include punctuation.
    final val addressLinesFormat = BusinessName.Pattern.r // to be applied on combined address lines

    // This is being left for backwards compatibility
    final val Mapping: Mapping[AddressAndPostcodeViewModel] = mapping(
      UprnId -> uprn,
      AddressLinesId -> addressLinesMapping().verifying(validAddressLines),
      PostcodeId -> postcode
    )(AddressAndPostcodeViewModel.apply)(AddressAndPostcodeViewModel.unapply)

    def mappingWithCustomPostTownMaxLength(postTownMaxLength: Int): Mapping[AddressAndPostcodeViewModel] =
      play.api.data.Forms.mapping(
        UprnId -> uprn,
        AddressLinesId -> addressLinesMapping(postTownMaxLength).verifying(validAddressLines),
        PostcodeId -> postcode
      )(AddressAndPostcodeViewModel.apply)(AddressAndPostcodeViewModel.unapply)

    private def uprn: Mapping[Option[Int]] = optional(number)

    private def validAddressLines: Constraint[AddressLinesViewModel] = Constraint[AddressLinesViewModel](RequiredField) {
      case input: AddressLinesViewModel =>

        val addressLines = input.toViewFormat.dropRight(1).mkString

        val postTown = input.toViewFormat.last.mkString

//        if (input.totalCharacters > MaxLengthOfLinesConcatenated)
//          Invalid(ValidationError("error.address.maxLengthOfLinesConcatenated"))
        if (!addressLinesFormat.pattern.matcher(addressLines).matches)
          Invalid(ValidationError("error.address.characterInvalid"))
        else if (!postTownFormat.pattern.matcher(postTown).matches)
          Invalid(ValidationError("error.address.postTown.characterInvalid"))
        else if (!buildingNameOrNumberFormat.pattern.matcher(input.buildingNameOrNumber).matches)
          Invalid(ValidationError("error.address.threeAlphas"))
        else Valid

      case _ => Invalid(ValidationError("error.address.buildingNameOrNumber.invalid"))
    }
  }
}
