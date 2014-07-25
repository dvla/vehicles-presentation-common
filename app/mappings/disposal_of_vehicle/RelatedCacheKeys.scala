package mappings.disposal_of_vehicle

import mappings.common.Help.HelpCacheKey
import viewmodels.BusinessChooseYourAddressViewModel.BusinessChooseYourAddressCacheKey
import viewmodels.DisposeFormViewModel.{DisposeFormModelCacheKey, DisposeFormRegistrationNumberCacheKey, DisposeFormTimestampIdCacheKey, DisposeFormTransactionIdCacheKey, DisposeOccurredCacheKey, PreventGoingToDisposePageCacheKey}
import viewmodels.DisposeViewModel.DisposeModelCacheKey
import viewmodels.SetupTradeDetailsViewModel.SetupTradeDetailsCacheKey
import viewmodels.TraderDetailsViewModel.TraderDetailsCacheKey
import viewmodels.VehicleDetailsViewModel.VehicleLookupDetailsCacheKey
import viewmodels.VehicleLookupFormViewModel.{VehicleLookupFormModelCacheKey, VehicleLookupResponseCodeCacheKey}
import controllers.disposal_of_vehicle.MicroServiceError.MicroServiceErrorRefererCacheKey
import viewmodels.{BruteForcePreventionViewModel, EnterAddressManuallyViewModel}
import EnterAddressManuallyViewModel.EnterAddressManuallyCacheKey
import BruteForcePreventionViewModel.BruteForcePreventionViewModelCacheKey

object RelatedCacheKeys {
  final val SeenCookieMessageKey = "seen_cookie_message"

  // TODO: what is this set of cookies for?
  val DisposeOnlySet = Set(
    DisposeFormModelCacheKey,
    DisposeFormTransactionIdCacheKey,
    DisposeFormTimestampIdCacheKey,
    DisposeFormRegistrationNumberCacheKey,
    DisposeModelCacheKey
  )

  // Set of cookies related to a single vehicle disposal. Removed once the vehicle is successfully disposed
  val DisposeSet = Set(
    BruteForcePreventionViewModelCacheKey,
    VehicleLookupDetailsCacheKey,
    VehicleLookupResponseCodeCacheKey,
    VehicleLookupFormModelCacheKey,
    DisposeFormModelCacheKey,
    DisposeFormTransactionIdCacheKey,
    DisposeFormTimestampIdCacheKey,
    DisposeFormRegistrationNumberCacheKey,
    DisposeModelCacheKey
  )

  // Set of cookies that store the trade details data. These are retained after a successful disposal
  // so the trader does not have to re-enter their details when disposing subsequent vehicles
  val TradeDetailsSet = Set(SetupTradeDetailsCacheKey,
      TraderDetailsCacheKey,
      BusinessChooseYourAddressCacheKey,
      EnterAddressManuallyCacheKey)

  // The full set of cache keys. These are removed at the start of the process in the "before_you_start" page
  val FullSet = TradeDetailsSet.++(DisposeSet)
                              .++(Set(PreventGoingToDisposePageCacheKey))
                              .++(Set(DisposeOccurredCacheKey))
                              .++(Set(HelpCacheKey))
                              .++(Set(MicroServiceErrorRefererCacheKey))
}
