package models

import play.api.libs.json.Json
import services.DateService
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import webserviceclients.brute_force_prevention.BruteForcePreventionResponseDto

final case class BruteForcePreventionModel(permitted: Boolean,
                                               attempts: Int,
                                               maxAttempts: Int,
                                               dateTimeISOChronology: String)

object BruteForcePreventionModel {
  implicit final val JsonFormat = Json.format[BruteForcePreventionModel]
  final val BruteForcePreventionViewModelCacheKey = "bruteForcePreventionViewModel"
  implicit final val Key = CacheKey[BruteForcePreventionModel](BruteForcePreventionViewModelCacheKey)

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
