package services.address_lookup.ordnance_survey

import play.api.libs.json.Json

final case class PostcodeToAddressResponseDto(addresses: Seq[UprnAddressPairDto])

object PostcodeToAddressResponseDto{
  implicit val JsonFormat = Json.format[PostcodeToAddressResponseDto]
}
