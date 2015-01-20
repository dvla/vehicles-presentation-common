package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire_service

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties._
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire.AcquireConfig
import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeAcquireConfig extends AcquireConfig {
  override lazy val baseUrl = getOptionalProperty[String]("acquireVehicle.baseUrl").getOrElse("")
  override lazy val requestTimeout = getOptionalProperty[Int]("acquireVehicle.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
}
