package viewmodels

import models.DayMonthYear
import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class DisposeFormViewModel(mileage: Option[Int],
                                  dateOfDisposal: DayMonthYear,
                                  consent: String,
                                  lossOfRegistrationConsent: String)

object DisposeFormViewModel {
  implicit val JsonFormat = Json.format[DisposeFormViewModel]
  final val DisposeFormModelCacheKey = "disposeForm"
  implicit val Key = CacheKey[DisposeFormViewModel](value = DisposeFormModelCacheKey)
  final val DisposeOccurredCacheKey = "disposeOccurredCacheKey"
  final val PreventGoingToDisposePageCacheKey = "preventGoingToDisposePage"
  final val DisposeFormTransactionIdCacheKey = "disposeFormTransactionId"
  final val DisposeFormTimestampIdCacheKey = "disposeFormTimestampId"
  final val DisposeFormRegistrationNumberCacheKey = "disposeFormRegistrationNumber"
}
