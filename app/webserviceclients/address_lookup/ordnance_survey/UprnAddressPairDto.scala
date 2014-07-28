package webserviceclients.address_lookup.ordnance_survey

import play.api.libs.json.Json

final case class UprnAddressPairDto(uprn: String, address: String)

object UprnAddressPairDto {
  implicit val JsonFormat = Json.format[UprnAddressPairDto]
}
