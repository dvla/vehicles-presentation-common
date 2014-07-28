package mappings.disposal_of_vehicle

import controllers.disposal_of_vehicle.MicroServiceError.MicroServiceErrorRefererCacheKey
import mappings.common.Help.HelpCacheKey
import viewmodels.BruteForcePreventionViewModel.BruteForcePreventionViewModelCacheKey
import viewmodels.BusinessChooseYourAddressViewModel.BusinessChooseYourAddressCacheKey
import viewmodels.DisposeFormViewModel.DisposeFormModelCacheKey
import viewmodels.DisposeFormViewModel.DisposeFormRegistrationNumberCacheKey
import viewmodels.DisposeFormViewModel.DisposeFormTimestampIdCacheKey
import viewmodels.DisposeFormViewModel.DisposeFormTransactionIdCacheKey
import viewmodels.DisposeFormViewModel.DisposeOccurredCacheKey
import viewmodels.DisposeFormViewModel.PreventGoingToDisposePageCacheKey
import viewmodels.DisposeViewModel.DisposeModelCacheKey
import viewmodels.EnterAddressManuallyViewModel.EnterAddressManuallyCacheKey
import viewmodels.SetupTradeDetailsViewModel.SetupTradeDetailsCacheKey
import viewmodels.TraderDetailsViewModel.TraderDetailsCacheKey
import viewmodels.VehicleDetailsViewModel.VehicleLookupDetailsCacheKey
import viewmodels.VehicleLookupFormViewModel.{VehicleLookupFormModelCacheKey, VehicleLookupResponseCodeCacheKey}

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