package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{booleanProp, getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class BruteForcePreventionConfig {

  lazy val baseUrl: String = getOptionalProperty[String]("bruteForcePrevention.baseUrl").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  lazy val requestTimeoutMillis: Int = getOptionalProperty[Int]("bruteForcePrevention.requestTimeout").getOrElse(CommonConfig.DEFAULT_BF_REQ_TIMEOUT.seconds.toMillis.toInt)
  lazy val isEnabled: Boolean = getOptionalProperty[Boolean]("bruteForcePrevention.enabled").getOrElse(CommonConfig.DEFAULT_BF_ENABLED)
  lazy val nameHeader: String = getOptionalProperty[String]("bruteForcePrevention.headers.serviceName").getOrElse(CommonConfig.DEFAULT_BF_SERVICE_NAME)
  lazy val maxAttemptsHeader: Int = getOptionalProperty[Int]("bruteForcePrevention.headers.maxAttempts").getOrElse(CommonConfig.DEFAULT_BF_MAX_ATTEMPTS)
  lazy val expiryHeader: String = getOptionalProperty[String]("bruteForcePrevention.headers.expiry").getOrElse(CommonConfig.DEFAULT_BF_EXPIRY)
}
