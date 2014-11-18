package uk.gov.dvla.vehicles.presentation.common.views.models

import play.api.data.Forms.{mapping, number, optional}
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.views.models.AddressLinesViewModel.Form.{AddressLinesId, mapping => addressLinesMapping}
import common.views.constraints.Required.RequiredField

case class AddressAndPostcodeViewModel(uprn: Option[Int] = None, addressLinesModel: AddressLinesViewModel) {
  def toViewFormat(postcode: String): Seq[String] = addressLinesModel.toViewFormat :+ postcode
}

object AddressAndPostcodeViewModel {
  implicit val AddressAndPostcodeModelFormat = Json.format[AddressAndPostcodeViewModel]
  final val AddressAndPostcodeCacheKey = "addressAndPostcodeCacheKey"
  implicit val Key = CacheKey[AddressAndPostcodeViewModel](AddressAndPostcodeCacheKey)

  object Form {
    final val UprnId = "uprn"
    final val MaxLengthOfLinesConcatenated = 120

    // This is being left for backwards compatibility
    final val Mapping: Mapping[AddressAndPostcodeViewModel] = mapping(
      UprnId -> uprn,
      AddressLinesId -> addressLinesMapping().verifying(validAddressLines)
    )(AddressAndPostcodeViewModel.apply)(AddressAndPostcodeViewModel.unapply)

    def mappingWithCustomPostTownMaxLength(postTownMaxLength: Int): Mapping[AddressAndPostcodeViewModel] =
      play.api.data.Forms.mapping(
        UprnId -> uprn,
        AddressLinesId -> addressLinesMapping(postTownMaxLength).verifying(validAddressLines)
      )(AddressAndPostcodeViewModel.apply)(AddressAndPostcodeViewModel.unapply)

    private def uprn: Mapping[Option[Int]] = optional(number)

    private def validAddressLines: Constraint[AddressLinesViewModel] = Constraint[AddressLinesViewModel](RequiredField) {
      case input: AddressLinesViewModel =>
        // Regex states string must contain at least one number or letter, can also include punctuation.
        val addressLinesFormat = """^[a-zA-Z0-9][A-Za-z0-9\s\-\,\.\/\\]*$""".r

        // TODO FIX THIS CODE WHICH DOESN'T DO WHAT YOU MIGHT EXPECT

        val addressLines = input.toViewFormat.dropRight(1).mkString

        // Post town cannot contain numbers, can also include punctuation.
        val postTownFormat = """^[a-zA-Z][A-Za-z\s\-\,\.\/\\]*$""".r

        val postTown = input.toViewFormat.last.mkString

        if (input.totalCharacters > MaxLengthOfLinesConcatenated)
          Invalid(ValidationError("error.address.maxLengthOfLinesConcatenated"))
        else if (!addressLinesFormat.pattern.matcher(addressLines).matches)
          Invalid(ValidationError("error.address.characterInvalid"))
        else if (!postTownFormat.pattern.matcher(postTown).matches)
          Invalid(ValidationError("error.postTown.characterInvalid"))
        else Valid

      case _ => Invalid(ValidationError("error.address.buildingNameOrNumber.invalid"))
    }
  }
}
