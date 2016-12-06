package uk.gov.dvla.vehicles.presentation.common.utils.helpers

trait CommonConfig {
  def isPrototypeBannerVisible: Boolean
}

object CommonConfig {
  final val DEFAULT_APPLICATION_CONTEXT = ""
  final val DEFAULT_BASE_URL = ""
  final val DEFAULT_SECURE_COOKIES = true
  final val DEFAULT_CSRF_PREVENTION = true
  final val DEFAULT_REQ_TIMEOUT = 5 // in seconds
  final val DEFAULT_VKL_REQ_TIMEOUT = 2 * DEFAULT_REQ_TIMEOUT // in seconds

  // brute force
  final val DEFAULT_BF_ENABLED = true
  final val DEFAULT_BF_SERVICE_NAME = ""
  final val DEFAULT_BF_MAX_ATTEMPTS = 3
  final val DEFAULT_BF_EXPIRY = ""
  final val DEFAULT_BF_REQ_TIMEOUT = 2 * DEFAULT_REQ_TIMEOUT

  final val DEFAULT_HEALTHSTATS = -1 // general rogue value
  final val DEFAULT_HEALTHSTATS_L = -1L // general rogue value
}
