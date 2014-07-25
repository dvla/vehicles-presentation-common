package models.domain.disposal_of_vehicle

import play.api.libs.json.Json

final case class DisposeResponseDto(transactionId: String,
                                  registrationNumber: String,
                                  auditId: String,
                                  responseCode: Option[String] = None)

object DisposeResponseDto{
  implicit val JsonFormat = Json.format[DisposeResponseDto]
}
