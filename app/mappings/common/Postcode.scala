package mappings.common

import views.constraints.Postcode.validPostcode
import play.api.data.Mapping
import views.helpers.FormExtensions
import FormExtensions.nonEmptyTextWithTransform

object Postcode {
  final val PostcodeId = "postcode"
  final val Key = "postCode"
  private final val MinLength = 5
  final val MaxLength = 8

  def postcode: Mapping[String] = {
    nonEmptyTextWithTransform(_.toUpperCase.trim)(MinLength, MaxLength) verifying validPostcode
  }
}