package viewmodels

import models.domain.common.AddressAndPostcodeModel
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

final case class EnterAddressManuallyViewModel(addressAndPostcodeModel: AddressAndPostcodeModel)

object EnterAddressManuallyViewModel {
  implicit val JsonFormat = Json.format[EnterAddressManuallyViewModel]

  final val EnterAddressManuallyCacheKey = "enterAddressManually"
  implicit val Key = CacheKey[EnterAddressManuallyViewModel](EnterAddressManuallyCacheKey)

  final val AddressAndPostcodeId = "addressAndPostcode"
  final val FormMapping = mapping(
    AddressAndPostcodeId -> AddressAndPostcodeModel.FormMapping
  )(EnterAddressManuallyViewModel.apply)(EnterAddressManuallyViewModel.unapply)
}
