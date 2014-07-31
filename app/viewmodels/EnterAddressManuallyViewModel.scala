package viewmodels

import models.domain.common.CacheKey
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import viewmodels.common.AddressAndPostcodeViewModel

final case class EnterAddressManuallyViewModel(addressAndPostcodeModel: AddressAndPostcodeViewModel)

object EnterAddressManuallyViewModel {
  implicit val JsonFormat = Json.format[EnterAddressManuallyViewModel]

  final val EnterAddressManuallyCacheKey = "enterAddressManually"
  implicit val Key = CacheKey[EnterAddressManuallyViewModel](EnterAddressManuallyCacheKey)

  final val AddressAndPostcodeId = "addressAndPostcode"
  final val FormMapping = mapping(
    AddressAndPostcodeId -> AddressAndPostcodeViewModel.Form.Mapping
  )(EnterAddressManuallyViewModel.apply)(EnterAddressManuallyViewModel.unapply)
}
