package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.model.CommonCacheKeys.CacheKeyPrefix
import uk.gov.dvla.vehicles.presentation.common.mappings.DropDown

final case class NewKeeperChooseYourAddressFormModel(uprnSelected: String)

object NewKeeperChooseYourAddressFormModel {
  implicit val JsonFormat = Json.format[NewKeeperChooseYourAddressFormModel]
  final val NewKeeperChooseYourAddressCacheKey = s"${CacheKeyPrefix}newKeeperChooseYourAddress"
  implicit val Key = CacheKey[NewKeeperChooseYourAddressFormModel](value = NewKeeperChooseYourAddressCacheKey)

  object Form {
    final val AddressSelectId = "acquire_newKeeperChooseYourAddress_addressSelect"
    final val Mapping = mapping(
      AddressSelectId -> DropDown.addressDropDown
    )(NewKeeperChooseYourAddressFormModel.apply)(NewKeeperChooseYourAddressFormModel.unapply)
  }
}
