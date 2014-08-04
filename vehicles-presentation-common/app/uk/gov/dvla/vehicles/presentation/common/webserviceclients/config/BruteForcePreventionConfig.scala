package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class BruteForcePreventionConfig {
  val baseUrl = getProperty("bruteForcePrevention.baseUrl", "NOT FOUND")
  val requestTimeout = getProperty("bruteForcePrevention.requestTimeout", 5.seconds.toMillis.toInt)
  val isEnabled = getProperty("bruteForcePrevention.enabled", default = true)
  val nameHeader = getProperty("bruteForcePrevention.headers.serviceName", "")
  val maxAttemptsHeader = getProperty("bruteForcePrevention.headers.maxAttempts", 3)
  val expiryHeader = getProperty("bruteForcePrevention.headers.expiry", "")
}
