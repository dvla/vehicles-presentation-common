package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp, intProp, booleanProp}

class BruteForcePreventionConfig {

  lazy val baseUrl: String = getOptionalProperty[String]("bruteForcePrevention.baseUrl").getOrElse("")
  lazy val requestTimeoutMillis: Int = getOptionalProperty[Int]("bruteForcePrevention.requestTimeout").getOrElse(10000)
  lazy val isEnabled: Boolean = getOptionalProperty[Boolean]("bruteForcePrevention.enabled").getOrElse(true)
  lazy val nameHeader: String = getOptionalProperty[String]("bruteForcePrevention.headers.serviceName").getOrElse("")
  lazy val maxAttemptsHeader: Int = getOptionalProperty[Int]("bruteForcePrevention.headers.maxAttempts").getOrElse(3)
  lazy val expiryHeader: String = getOptionalProperty[String]("bruteForcePrevention.headers.expiry").getOrElse("")
}
