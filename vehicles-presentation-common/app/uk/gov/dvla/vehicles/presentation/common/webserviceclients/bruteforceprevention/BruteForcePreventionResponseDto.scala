package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import play.api.libs.json.Json

final case class BruteForcePreventionResponseDto(attempts: Int)

object BruteForcePreventionResponseDto {
  implicit val JsonFormat = Json.format[BruteForcePreventionResponseDto]
}
