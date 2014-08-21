package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import play.api.libs.json.Json

final case class UprnAddressPairDto(uprn: String, address: String)

object UprnAddressPairDto {
  implicit val JsonFormat = Json.format[UprnAddressPairDto]
}
