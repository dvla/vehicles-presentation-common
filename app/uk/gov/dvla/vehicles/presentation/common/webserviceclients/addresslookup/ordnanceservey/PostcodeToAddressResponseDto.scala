package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import play.api.libs.json.Json

final case class PostcodeToAddressResponseDto(addresses: Seq[AddressResponseDto])

object PostcodeToAddressResponseDto{
  implicit val JsonFormat = Json.format[PostcodeToAddressResponseDto]
}
