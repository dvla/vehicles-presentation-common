package uk.gov.dvla.vehicles.presentation.common

package object model {

  def newKeeperEnterAddressManuallyCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}newKeeperEnterAddressManually"
  def allowGoingToCompleteAndConfirmPageCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}allowGoingToCompleteAndConfirmPage"
}
