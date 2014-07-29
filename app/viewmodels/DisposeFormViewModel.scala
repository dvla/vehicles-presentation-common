package viewmodels

import models.DayMonthYear
import models.domain.common.CacheKey
import play.api.data.Mapping
import play.api.libs.json.Json
import services.DateService
import constraints.common.DayMonthYear.{validDate, after, notInFuture}
import mappings.common.Consent.consent
import mappings.common.DayMonthYear.dayMonthYear
import mappings.common.Mileage.mileage
import mappings.disposal_of_vehicle.Dispose.MileageId
import mappings.disposal_of_vehicle.Dispose.DateOfDisposalId
import mappings.disposal_of_vehicle.Dispose.DateOfDisposalYearsIntoThePast
import mappings.disposal_of_vehicle.Dispose.ConsentId
import mappings.disposal_of_vehicle.Dispose.LossOfRegistrationConsentId

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

  object Form {
    def mapping(dateService: DateService): Mapping[DisposeFormViewModel] =
      play.api.data.Forms.mapping(
        MileageId -> mileage(),
        DateOfDisposalId -> dayMonthYear.verifying(validDate(),
          after(earliest = (dateService.today - DateOfDisposalYearsIntoThePast).years),
          notInFuture(dateService)),
        ConsentId -> consent,
        LossOfRegistrationConsentId -> consent
      )(DisposeFormViewModel.apply)(DisposeFormViewModel.unapply)
  }
}
