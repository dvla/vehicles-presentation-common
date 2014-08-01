package viewmodels

import constraints.TraderBusinessName
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode
import Postcode.postcode
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.views.helpers.FormExtensions
import FormExtensions._

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
