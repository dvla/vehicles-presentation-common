package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

import play.api.libs.json.Json

final case class VssWebEndUserDto (orgBusUnit: String,
                                   endUserId: String)

object VssWebEndUserDto {

  implicit val JsonFormat = Json.format[VssWebEndUserDto]
}
