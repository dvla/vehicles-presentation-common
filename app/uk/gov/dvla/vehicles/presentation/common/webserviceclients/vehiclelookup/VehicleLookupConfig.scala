package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty

class VehicleLookupConfig {
  lazy val baseUrl = getProperty[String]("vehicleLookup.baseUrl")
}
