package uk.gov.dvla.vehicles.presentation.common.k2kandacquire.models

import play.api.data.Forms.{mapping, optional}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.k2kandacquire._
import uk.gov.dvla.vehicles.presentation.common.mappings.BusinessKeeperName.businessKeeperNameMapping
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.email
import uk.gov.dvla.vehicles.presentation.common.mappings.FleetNumber.fleetNumberMapping
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode.postcode

final case class BusinessKeeperDetailsFormModel(fleetNumber: Option[String],
                                                businessName: String,
                                                email: Option[String],
                                                postcode: String)

object BusinessKeeperDetailsFormModel {
  implicit val JsonFormat = Json.format[BusinessKeeperDetailsFormModel]
  final val BusinessKeeperDetailsCacheKey = s"${CacheKeyPrefix}businessKeeperDetails"
  implicit val Key = CacheKey[BusinessKeeperDetailsFormModel](BusinessKeeperDetailsCacheKey)

  object Form {
    final val FleetNumberId = "fleetNumber"
    final val BusinessNameId = "businessName"
    final val EmailId = "businesskeeper_email"
    final val PostcodeId = "businesskeeper_postcode"

    final val Mapping = mapping(
      FleetNumberId -> fleetNumberMapping,
      BusinessNameId -> businessKeeperNameMapping,
      EmailId -> optional(email),
      PostcodeId -> postcode
    )(BusinessKeeperDetailsFormModel.apply)(BusinessKeeperDetailsFormModel.unapply)
  }
}