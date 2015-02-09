package uk.gov.dvla.vehicles.presentation.common

import uk.gov.dvla.vehicles.presentation.common
import common.model.BruteForcePreventionModel.BruteForcePreventionViewModelCacheKey
import common.model.TraderDetailsModel.TraderDetailsCacheKey
import common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
//import models.BusinessChooseYourAddressFormModel.BusinessChooseYourAddressCacheKey
//import models.BusinessKeeperDetailsFormModel.BusinessKeeperDetailsCacheKey
//import models.EnterAddressManuallyFormModel.EnterAddressManuallyCacheKey
//import models.SetupTradeDetailsFormModel.SetupTradeDetailsCacheKey
//import models.CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
//import models.PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey
//import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
//import models.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
//import models.NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey
//import models.NewKeeperEnterAddressManuallyFormModel.NewKeeperEnterAddressManuallyCacheKey
//import models.NewKeeperDetailsViewModel.NewKeeperDetailsCacheKey
//import models.VehicleTaxOrSornFormModel.VehicleTaxOrSornCacheKey
//import models.CompleteAndConfirmResponseModel.AcquireCompletionResponseCacheKey

package object k2kandacquire {
  final val CacheKeyPrefix = "acq-"
  final val HelpCacheKey = s"${CacheKeyPrefix}help"
  final val SeenCookieMessageCacheKey = "seen_cookie_message" // Same value across all exemplars

//  final val AcquireCacheKeys = Set(
//    NewKeeperChooseYourAddressCacheKey,
//    BruteForcePreventionViewModelCacheKey
//  )



  // Vehicle, new keeper and completion cache keys are removed. Trader cache keys remain
//  final val VehicleNewKeeperCompletionCacheKeys =
//    AcquireCacheKeys
//    .++(VehicleLookupCacheKeys)
//    .++(PrivateKeeperDetailsCacheKeys)
//    .++(BusinessKeeperDetailsCacheKeys)
//    .++(CompletionCacheKeys)
//    .++(Set(HelpCacheKey))

  // The full set of cache keys. These are removed at the start of the process in the "before_you_start" page
//  final val AllCacheKeys =
//    VehicleNewKeeperCompletionCacheKeys
//    .++(TraderDetailsCacheKeys)
}
