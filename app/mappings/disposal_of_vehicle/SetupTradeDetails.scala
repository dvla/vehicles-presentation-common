package mappings.disposal_of_vehicle

import constraints.disposal_of_vehicle.TraderBusinessName
import play.api.data.Mapping
import utils.helpers.FormExtensions.nonEmptyTextWithTransform

object SetupTradeDetails {
  final val TraderNameMaxLength = 58
  final val TraderNameMinLength = 2
  final val TraderNameId = "traderName"

  final val TraderPostcodeId = "traderPostcode"
  final val SubmitId = "submit"

  def traderBusinessName (minLength: Int = TraderNameMinLength,
                          maxLength: Int = TraderNameMaxLength): Mapping[String] = {
    nonEmptyTextWithTransform(_.toUpperCase.trim)(minLength, maxLength)
      .verifying(TraderBusinessName.validTraderBusinessName)
  }
}