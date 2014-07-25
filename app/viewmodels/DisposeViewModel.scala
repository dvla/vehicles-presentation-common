package viewmodels

import models.DayMonthYear
import models.domain.common.CacheKey
import play.api.libs.json.{Format, Json}

final case class DisposeViewModel(referenceNumber: String,
                              registrationNumber: String,
                              dateOfDisposal: DayMonthYear,
                              consent: String,
                              lossOfRegistrationConsent: String,
                              mileage: Option[Int])

object DisposeViewModel {
  implicit val JsonFormat: Format[DisposeViewModel] = Json.format[DisposeViewModel]
  final val DisposeModelCacheKey = "formModel"
  implicit val Key = CacheKey[DisposeViewModel](value = DisposeModelCacheKey)
}
