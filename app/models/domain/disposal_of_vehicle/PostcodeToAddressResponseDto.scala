package models.domain.disposal_of_vehicle

import play.api.libs.json.Json

final case class PostcodeToAddressResponseDto(addresses: Seq[UprnAddressPairDto])

object PostcodeToAddressResponseDto{
  implicit val JsonFormat = Json.format[PostcodeToAddressResponseDto]
}
