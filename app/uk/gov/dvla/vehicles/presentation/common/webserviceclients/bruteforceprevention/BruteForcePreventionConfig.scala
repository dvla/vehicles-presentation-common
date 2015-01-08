package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class BruteForcePreventionConfig {

//  val baseUrl: String = getProperty("bruteForcePrevention.baseUrl", "NOT FOUND")
//  val requestTimeoutMillis: Int = getProperty("bruteForcePrevention.requestTimeout", 5.seconds.toMillis.toInt)
//  val isEnabled: Boolean = getProperty("bruteForcePrevention.enabled", default = true)
//  val nameHeader: String = getProperty("bruteForcePrevention.headers.serviceName", "")
//  val maxAttemptsHeader: Int = getProperty("bruteForcePrevention.headers.maxAttempts", 3)
//  val expiryHeader: String = getProperty("bruteForcePrevention.headers.expiry", "")

  lazy val baseUrl: String = getProperty[String]("bruteForcePrevention.baseUrl")
  lazy val requestTimeoutMillis: Int = getProperty[Int]("bruteForcePrevention.requestTimeout")
  lazy val isEnabled: Boolean = getProperty[Boolean]("bruteForcePrevention.enabled")
  lazy val nameHeader: String = getProperty[String]("bruteForcePrevention.headers.serviceName")
  lazy val maxAttemptsHeader: Int = getProperty[Int]("bruteForcePrevention.headers.maxAttempts")
  lazy val expiryHeader: String = getProperty[String]("bruteForcePrevention.headers.expiry")
}
