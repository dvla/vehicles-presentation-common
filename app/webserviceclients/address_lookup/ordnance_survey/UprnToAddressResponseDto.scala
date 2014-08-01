package webserviceclients.address_lookup.ordnance_survey

import models.AddressModel
import play.api.libs.json.Json

final case class UprnToAddressResponseDto(addressViewModel: Option[AddressModel])

object UprnToAddressResponseDto {
  implicit val JsonFormat = Json.format[UprnToAddressResponseDto]
}
