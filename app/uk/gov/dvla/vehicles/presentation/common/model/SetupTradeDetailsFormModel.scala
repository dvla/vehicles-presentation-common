package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.BusinessName.businessNameMapping
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.email
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode.postcode

final case class SetupTradeDetailsFormModel(traderBusinessName: String,
                                            traderPostcode: String,
                                            traderEmail: Option[String])

object SetupTradeDetailsFormModel {
  implicit val JsonFormat = Json.format[SetupTradeDetailsFormModel]

  implicit def key(implicit prefix: CacheKeyPrefix): CacheKey[SetupTradeDetailsFormModel] =
    CacheKey[SetupTradeDetailsFormModel](setupTradeDetailsCacheKey)

  def setupTradeDetailsCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}setupTraderDetails"

  object Form {
    final val TraderNameId = "traderName"
    final val TraderPostcodeId = "traderPostcode"
    final val TraderEmailId = "traderEmail"
    final val TraderEmailOptionId = "traderEmailOption"

    final val Mapping = mapping(
      TraderNameId -> businessNameMapping,
      TraderPostcodeId -> postcode,
      TraderEmailOptionId -> OptionalToggle.optional(email.withPrefix(TraderEmailId))
    )(SetupTradeDetailsFormModel.apply)(SetupTradeDetailsFormModel.unapply)
  }
}
