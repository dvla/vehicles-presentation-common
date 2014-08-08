package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty

class VehicleLookupConfig {
  val baseUrl = getProperty("vehicleLookup.baseUrl", "NOT FOUND")
}
