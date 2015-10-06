package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

import play.api.libs.json.Json

final case class MicroserviceResponse(code: String, message: String)

object MicroserviceResponse {
  implicit val JsonFormat = Json.format[MicroserviceResponse]
}
