package uk.gov.dvla.vehicles.presentation.common.webserviceclients.brute_force_prevention

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention.BruteForcePreventionConfig
import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeBruteForcePreventionConfig extends BruteForcePreventionConfig {

  override lazy val baseUrl: String = getOptionalProperty[String]("bruteForcePrevention.baseUrl").getOrElse("")
  override lazy val requestTimeoutMillis: Int = getOptionalProperty[Int]("bruteForcePrevention.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
  override lazy val isEnabled: Boolean = getOptionalProperty[Boolean]("bruteForcePrevention.enabled").getOrElse(false)
  override lazy val nameHeader: String = getOptionalProperty[String]("bruteForcePrevention.headers.serviceName").getOrElse("")
  override lazy val maxAttemptsHeader: Int = getOptionalProperty[Int]("bruteForcePrevention.headers.maxAttempts").getOrElse(3)
  override lazy val expiryHeader: String = getOptionalProperty[String]("bruteForcePrevention.headers.expiry").getOrElse("")

}
