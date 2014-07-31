package mappings.common

import constraints.common.AddressLines.validAddressLines
import mappings.common.AddressLines.addressLines
import mappings.common.Uprn.{UprnId, uprn}
import models.domain.common.AddressAndPostcodeModel
import play.api.data.Forms.mapping
import play.api.data.Mapping
import widgets.mappings.AddressLines.AddressLinesId

object AddressAndPostcode {
  final val AddressAndPostcodeCacheKey = "addressAndPostcodeCacheKey"

  val addressAndPostcode: Mapping[AddressAndPostcodeModel] = mapping(
    UprnId -> uprn,
    AddressLinesId -> addressLines.verifying(validAddressLines)
  )(AddressAndPostcodeModel.apply)(AddressAndPostcodeModel.unapply)
}
