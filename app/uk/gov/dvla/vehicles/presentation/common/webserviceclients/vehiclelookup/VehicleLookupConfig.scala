package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty

class VehicleLookupConfig {
  lazy val baseUrl = getOptionalProperty[String]("vehicleLookup.baseUrl").getOrElse("")
}
