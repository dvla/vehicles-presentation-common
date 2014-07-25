package models.domain.disposal_of_vehicle

import models.DayMonthYear
import models.domain.common.CacheKey
import play.api.libs.json.Json

final case class DisposeFormModel(mileage: Option[Int],
                                  dateOfDisposal: DayMonthYear,
                                  consent: String,
                                  lossOfRegistrationConsent: String)

object DisposeFormModel {
  implicit val JsonFormat = Json.format[DisposeFormModel]
  final val DisposeFormModelCacheKey = "disposeForm"
  implicit val Key = CacheKey[DisposeFormModel](value = DisposeFormModelCacheKey)
}