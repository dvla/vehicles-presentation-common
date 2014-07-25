package services.address_lookup.ordnance_survey

import play.api.libs.json.Json
import viewmodels.AddressViewModel

final case class UprnToAddressResponseDto(addressViewModel: Option[AddressViewModel])

object UprnToAddressResponseDto {
  implicit val JsonFormat = Json.format[UprnToAddressResponseDto]
}
