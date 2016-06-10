package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class AcquireConfig {
  lazy val baseUrl = getOptionalProperty[String]("acquireVehicle.baseUrl").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  lazy val requestTimeout = getOptionalProperty[Int]("acquireVehicle.requestTimeout").getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
