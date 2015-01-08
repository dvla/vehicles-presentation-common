package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class AcquireConfig {
  lazy val baseUrl = getProperty[String]("acquireVehicle.baseUrl")
  lazy val requestTimeout = getProperty[Int]("acquireVehicle.requestTimeout")
//  val requestTimeout = getProperty("acquireVehicle.requestTimeout", 5.seconds.toMillis.toInt)
}
