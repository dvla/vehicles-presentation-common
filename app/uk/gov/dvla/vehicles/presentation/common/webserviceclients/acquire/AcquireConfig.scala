package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, getProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class AcquireConfig {
  lazy val baseUrl = getProperty[String]("acquireVehicle.baseUrl")
  lazy val requestTimeout = getOptionalProperty[Int]("acquireVehicle.requestTimeout")
    .getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
