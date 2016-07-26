package uk.gov.dvla.vehicles.presentation.common.views.models

import play.api.data.Forms.mapping
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode.postcode
import uk.gov.dvla.vehicles.presentation.common.views.constraints.BusinessName
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required.RequiredField
import uk.gov.dvla.vehicles.presentation.common.views.models.AddressLinesViewModel.Form.{AddressLinesId, mapping => addressLinesMapping}

case class AddressAndPostcodeViewModel(addressLinesModel: AddressLinesViewModel,
                                       postCode: String) {
  def toViewFormat: Seq[String] = addressLinesModel.toViewFormat :+ postCode
}

object AddressAndPostcodeViewModel {
  implicit val AddressAndPostcodeModelFormat = Json.format[AddressAndPostcodeViewModel]
  final val AddressAndPostcodeCacheKey = "addressAndPostcodeCacheKey"
  implicit val Key = CacheKey[AddressAndPostcodeViewModel](AddressAndPostcodeCacheKey)

  object Form {
    final val PostcodeId = "postcode"
    // NOTE: total address line max is actually defined by line length x number of address lines i.e. 30 x 3 = 90

    // First line must contain at least 3 alpha characters
    final val buildingNameOrNumberFormat = """([^A-Za-z]*?[A-Za-z][^A-Za-z]*?){3,}""".r

    // Post town cannot contain numbers, can also include punctuation.
    final val postTownFormat = """^[a-zA-Z][A-Za-z\s\-\,\.\/\\]*$""".r

    final val addressLinesFormat = BusinessName.Pattern.r // to be applied on combined address lines

    final val Mapping: Mapping[AddressAndPostcodeViewModel] = mapping(
      AddressLinesId -> addressLinesMapping().verifying(validAddressLines),
      PostcodeId -> postcode
    )(AddressAndPostcodeViewModel.apply)(AddressAndPostcodeViewModel.unapply)

    private def validAddressLines: Constraint[AddressLinesViewModel] = Constraint[AddressLinesViewModel](RequiredField) {
      case input: AddressLinesViewModel =>

        val addressLines = input.toViewFormat.dropRight(1).mkString

        val postTown = input.toViewFormat.last.mkString

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
