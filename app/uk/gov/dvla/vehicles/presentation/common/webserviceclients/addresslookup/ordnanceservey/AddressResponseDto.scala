package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import play.api.libs.json.Json

final case class AddressResponseDto(address: String,
                                    businessName: Option[String])

object AddressResponseDto {
  implicit val JsonFormat = Json.format[AddressResponseDto]
}
