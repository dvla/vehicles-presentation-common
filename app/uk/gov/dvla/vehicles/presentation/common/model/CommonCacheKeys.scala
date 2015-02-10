package uk.gov.dvla.vehicles.presentation.common.model

object CommonCacheKeys {
  final val CacheKeyPrefix = "acq-"
  final val HelpCacheKey = s"${CacheKeyPrefix}help"
  final val SeenCookieMessageCacheKey = "seen_cookie_message" // Same value across all exemplars
}
