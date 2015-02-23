package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention.BruteForcePreventionResponseDto

final case class BruteForcePreventionModel(permitted: Boolean,
                                           attempts: Int,
                                           maxAttempts: Int,
                                           dateTimeISOChronology: String)

object BruteForcePreventionModel {
  implicit final val JsonFormat = Json.format[BruteForcePreventionModel]

  implicit def key(implicit prefix: CacheKeyPrefix) =
    CacheKey[BruteForcePreventionModel](value = bruteForcePreventionViewModelCacheKey)

  def bruteForcePreventionViewModelCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}bruteForcePreventionViewModel"

  def fromResponse(permitted: Boolean,
                   response: BruteForcePreventionResponseDto,
                   dateService: DateService,
                   maxAttempts: Int): BruteForcePreventionModel = {
    BruteForcePreventionModel(permitted,
      attempts = response.attempts + 1,
      maxAttempts = maxAttempts,
      dateTimeISOChronology = dateService.dateTimeISOChronology // Save the time we locked in case we need to display it on a page e.g. vrm-locked page.
    )
  }
}
