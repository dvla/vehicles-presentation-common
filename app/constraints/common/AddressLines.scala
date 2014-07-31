package constraints.common

import constraints.common.Required.RequiredField
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import viewmodels.common.AddressLinesViewModel

object AddressLines {
  final val MaxLengthOfLinesConcatenated = 120
  final val LineMaxLength = 30

  def validAddressLines: Constraint[AddressLinesViewModel] = Constraint[AddressLinesViewModel](RequiredField) {
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
