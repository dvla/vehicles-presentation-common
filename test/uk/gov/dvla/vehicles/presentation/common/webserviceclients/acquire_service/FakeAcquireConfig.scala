package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire_service

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire.AcquireConfig

import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeAcquireConfig extends AcquireConfig {
  override lazy val baseUrl = getOptionalProperty[String]("acquireVehicle.baseUrl").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  override lazy val requestTimeout = getOptionalProperty[Int]("acquireVehicle.requestTimeout").getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
