package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class VehicleAndKeeperLookupConfig {
  lazy val vehicleAndKeeperLookupMicroServiceBaseUrl: String =
    getOptionalProperty[String]("vehicleAndKeeperLookupMicroServiceUrlBase").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  lazy val requestTimeout: Int =
    getOptionalProperty[Int]("vehicleAndKeeperLookup.requestTimeout").getOrElse(CommonConfig.DEFAULT_VKL_REQ_TIMEOUT.seconds.toMillis.toInt)
}
