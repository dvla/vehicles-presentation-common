package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty

class VehicleAndKeeperLookupConfig {
  lazy val vehicleAndKeeperLookupMicroServiceBaseUrl: String = getOptionalProperty[String]("vehicleAndKeeperLookupMicroServiceUrlBase").getOrElse("")
}
