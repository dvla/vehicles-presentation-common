package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.views.models.AddressAndPostcodeViewModel

final case class NewKeeperEnterAddressManuallyFormModel(addressAndPostcodeModel: AddressAndPostcodeViewModel)

object NewKeeperEnterAddressManuallyFormModel {
  implicit val JsonFormat = Json.format[NewKeeperEnterAddressManuallyFormModel]

  def newKeeperEnterAddressManuallyCacheKey(implicit prefix: CacheKeyPrefix) =
    s"${prefix}newKeeperEnterAddressManually"

  implicit def key(implicit prefix: CacheKeyPrefix) =
    CacheKey[NewKeeperEnterAddressManuallyFormModel](value = newKeeperEnterAddressManuallyCacheKey)

  object Form {

    final val AddressAndPostcodeId = "addressAndPostcode"
    final val Mapping = mapping(
    AddressAndPostcodeId -> AddressAndPostcodeViewModel.Form.Mapping
    )(NewKeeperEnterAddressManuallyFormModel.apply)(NewKeeperEnterAddressManuallyFormModel.unapply)

  }

}
