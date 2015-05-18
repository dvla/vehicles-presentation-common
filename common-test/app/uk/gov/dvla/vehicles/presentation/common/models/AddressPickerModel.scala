package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Forms._
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.mapAddress
import uk.gov.dvla.vehicles.presentation.common.model.{SearchFields, Address}

case class AddressPickerModel(address1: Address, address2: Option[Address])

object AddressPickerModel {
  implicit val Key = CacheKey[AddressPickerModel]("test-AddressPickerModel")
  implicit val SearchFieldsFormat = Json.format[SearchFields]
  implicit val AddressJsonFormat = Json.format[Address]
  implicit val JsonFormat = Json.format[AddressPickerModel]

  object Form {
    final val datePicker1Id = "address-picker-1"
    final val datePicker2Id = "address-picker-2"

    final val Mapping = mapping(
      datePicker1Id -> mapAddress,
      datePicker2Id -> optional(mapAddress)
    )(AddressPickerModel.apply)(AddressPickerModel.unapply)
  }
}
