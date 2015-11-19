package uk.gov.dvla.vehicles.presentation.common

package object model {
  final val SeenCookieMessageCacheKey = "seen_cookie_message" // Same value across all exemplars

  def newKeeperEnterAddressManuallyCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}newKeeperEnterAddressManually"
  def allowGoingToCompleteAndConfirmPageCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}allowGoingToCompleteAndConfirmPage"
}
