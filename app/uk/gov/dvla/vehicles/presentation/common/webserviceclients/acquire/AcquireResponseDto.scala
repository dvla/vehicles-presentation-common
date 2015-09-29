package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.MicroserviceResponse

final case class AcquireResponse(transactionId: String, registrationNumber: String)

final case class AcquireResponseDto(response: Option[MicroserviceResponse], acquireResponse: AcquireResponse)

object AcquireResponse {
  implicit val JsonFormat = Json.format[AcquireResponse]
}

object AcquireResponseDto {
  implicit val JsonFormat = Json.format[AcquireResponseDto]
}
