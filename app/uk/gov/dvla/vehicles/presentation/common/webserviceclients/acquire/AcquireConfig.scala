package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty
import scala.concurrent.duration.DurationInt

class AcquireConfig {
  lazy val baseUrl = getOptionalProperty[String]("acquireVehicle.baseUrl").getOrElse("")
  lazy val requestTimeout = getOptionalProperty[Int]("acquireVehicle.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
}
