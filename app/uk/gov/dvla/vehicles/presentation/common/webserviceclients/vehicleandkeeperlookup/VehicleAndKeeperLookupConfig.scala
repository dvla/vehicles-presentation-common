package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp, intProp}

class VehicleAndKeeperLookupConfig {
  lazy val vehicleAndKeeperLookupMicroServiceBaseUrl: String =
    getOptionalProperty[String]("vehicleAndKeeperLookupMicroServiceUrlBase").getOrElse("")
  lazy val requestTimeout: Int =
    getOptionalProperty[Int]("vehicleAndKeeperLookup.requestTimeout").getOrElse(10.seconds.toMillis.toInt)
}
