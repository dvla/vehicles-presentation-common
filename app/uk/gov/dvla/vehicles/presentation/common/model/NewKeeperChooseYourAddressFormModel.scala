package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.DropDown

final case class NewKeeperChooseYourAddressFormModel(addressSelected: String)

object NewKeeperChooseYourAddressFormModel {
  implicit val JsonFormat = Json.format[NewKeeperChooseYourAddressFormModel]

  implicit def key(implicit prefix: CacheKeyPrefix) =
    CacheKey[NewKeeperChooseYourAddressFormModel](value = newKeeperChooseYourAddressCacheKey)

  def newKeeperChooseYourAddressCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}newKeeperChooseYourAddress"

  object Form {
    final val AddressSelectId = "newKeeperChooseYourAddress_addressSelect"
    final val Mapping = mapping(
      AddressSelectId -> DropDown.addressDropDown
    )(NewKeeperChooseYourAddressFormModel.apply)(NewKeeperChooseYourAddressFormModel.unapply)
  }
}
