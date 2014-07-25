package models.domain.disposal_of_vehicle

import play.api.libs.json.Json
import viewmodels.AddressViewModel

final case class UprnToAddressResponseDto(addressViewModel: Option[AddressViewModel])

object UprnToAddressResponseDto {
  implicit val JsonFormat = Json.format[UprnToAddressResponseDto]
}
