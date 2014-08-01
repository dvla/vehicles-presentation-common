import controllers.disposal_of_vehicle.MicroServiceError.MicroServiceErrorRefererCacheKey
import mappings.common.Help.HelpCacheKey
import models.BruteForcePreventionModel
import BruteForcePreventionModel.BruteForcePreventionViewModelCacheKey
import viewmodels.BusinessChooseYourAddressViewModel.BusinessChooseYourAddressCacheKey
import viewmodels.DisposeFormViewModel.{DisposeFormModelCacheKey, DisposeFormRegistrationNumberCacheKey, DisposeFormTimestampIdCacheKey, DisposeFormTransactionIdCacheKey, DisposeOccurredCacheKey, PreventGoingToDisposePageCacheKey}
import viewmodels.EnterAddressManuallyViewModel.EnterAddressManuallyCacheKey
import viewmodels.SetupTradeDetailsViewModel.SetupTradeDetailsCacheKey
import viewmodels.TraderDetailsViewModel.TraderDetailsCacheKey
import viewmodels.VehicleDetailsViewModel.VehicleLookupDetailsCacheKey
import viewmodels.VehicleLookupFormViewModel.{VehicleLookupFormModelCacheKey, VehicleLookupResponseCodeCacheKey}

package object viewmodels {
  final val SeenCookieMessageCacheKey = "seen_cookie_message"

  // TODO: what is this set of cookies for?
  final val DisposeOnlyCacheKeys = Set(
    DisposeFormModelCacheKey,
    DisposeFormTransactionIdCacheKey,
    DisposeFormTimestampIdCacheKey,
    DisposeFormRegistrationNumberCacheKey
  )

  // Set of cookies related to a single vehicle disposal. Removed once the vehicle is successfully disposed
  final val DisposeCacheKeys = Set(
    BruteForcePreventionViewModelCacheKey,
    VehicleLookupDetailsCacheKey,
    VehicleLookupResponseCodeCacheKey,
    VehicleLookupFormModelCacheKey,
    DisposeFormModelCacheKey,
    DisposeFormTransactionIdCacheKey,
    DisposeFormTimestampIdCacheKey,
    DisposeFormRegistrationNumberCacheKey
  )

  // Set of cookies that store the trade details data. These are retained after a successful disposal
  // so the trader does not have to re-enter their details when disposing subsequent vehicles
  final val TradeDetailsCacheKeys = Set(SetupTradeDetailsCacheKey,
    TraderDetailsCacheKey,
    BusinessChooseYourAddressCacheKey,
    EnterAddressManuallyCacheKey)

  // The full set of cache keys. These are removed at the start of the process in the "before_you_start" page
  final val AllCacheKeys = TradeDetailsCacheKeys.++(DisposeCacheKeys)
    .++(Set(PreventGoingToDisposePageCacheKey))
    .++(Set(DisposeOccurredCacheKey))
    .++(Set(HelpCacheKey))
    .++(Set(MicroServiceErrorRefererCacheKey))
}

