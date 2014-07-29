package viewmodels

import constraints.disposal_of_vehicle.TraderBusinessName
import mappings.common.Postcode.postcode
import models.domain.common.CacheKey
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.libs.json.Json
import utils.helpers.FormExtensions._

// TODO the names of the params repeat names from the model so refactor
final case class SetupTradeDetailsViewModel(traderBusinessName: String, traderPostcode: String)

object SetupTradeDetailsViewModel {
  implicit val JsonFormat = Json.format[SetupTradeDetailsViewModel]
  final val SetupTradeDetailsCacheKey = "setupTraderDetails"
  implicit val Key = CacheKey[SetupTradeDetailsViewModel](SetupTradeDetailsCacheKey)

  object Form {
    final val TraderNameId = "traderName"
    final val TraderPostcodeId = "traderPostcode"
    final val TraderNameMaxLength = 58
    final val TraderNameMinLength = 2

    private final val TraderNameMapping: Mapping[String] =
      nonEmptyTextWithTransform(_.toUpperCase.trim)(TraderNameMinLength, TraderNameMaxLength)
        .verifying(TraderBusinessName.validTraderBusinessName)

    final val Mapping = mapping(
      TraderNameId -> TraderNameMapping,
      TraderPostcodeId -> postcode
    )(SetupTradeDetailsViewModel.apply)(SetupTradeDetailsViewModel.unapply)
  }
}
