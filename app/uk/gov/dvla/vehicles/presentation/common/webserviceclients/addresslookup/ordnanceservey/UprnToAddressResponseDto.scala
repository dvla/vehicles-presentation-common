package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel

final case class UprnToAddressResponseDto(addressViewModel: Option[AddressModel])

object UprnToAddressResponseDto {
  implicit val JsonFormat = Json.format[UprnToAddressResponseDto]
}
