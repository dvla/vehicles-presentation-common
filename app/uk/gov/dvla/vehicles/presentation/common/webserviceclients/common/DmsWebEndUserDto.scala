package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

import play.api.libs.json.Json

final case class DmsWebEndUserDto(endUserTeamCode: String,
                                  endUserTeamDesc: String,
                                  endUserRole: String,
                                  endUserId: String,
                                  endUserIdDesc: String,
                                  endUserLongNameDesc: String)

object DmsWebEndUserDto {

  implicit val JsonFormat = Json.format[DmsWebEndUserDto]
}
