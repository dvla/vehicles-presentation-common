package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms.{mapping, optional}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.BusinessKeeperName.businessKeeperNameMapping
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.email
import uk.gov.dvla.vehicles.presentation.common.mappings.FleetNumber.fleetNumberMapping
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode.postcode

final case class BusinessKeeperDetailsFormModel(fleetNumber: Option[String],
                                                businessName: String,
                                                email: Option[String],
                                                postcode: String)

object BusinessKeeperDetailsFormModel {
  implicit val JsonFormat = Json.format[BusinessKeeperDetailsFormModel]

  implicit def key(implicit prefix: CacheKeyPrefix) =
    CacheKey[BusinessKeeperDetailsFormModel](businessKeeperDetailsCacheKey)

  def businessKeeperDetailsCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}businessKeeperDetails"

  object Form {
    final val FleetNumberId = "fleetNumber"
    final val BusinessNameId = "businessName"
    final val EmailId = "businesskeeper_email"
    final val EmailOptionId = "businesskeeper_option_email"
    final val PostcodeId = "businesskeeper_postcode"

    final val Mapping = mapping(
      FleetNumberId -> fleetNumberMapping,
      BusinessNameId -> businessKeeperNameMapping,
      EmailOptionId -> OptionalToggle.optional(email.withPrefix(EmailId)),
      PostcodeId -> postcode
    )(BusinessKeeperDetailsFormModel.apply)(BusinessKeeperDetailsFormModel.unapply)
  }
}
