package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicle_lookup

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties._
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.VehicleLookupConfig


/**
 * Fake Configuration that should be used in all the tests
 */
class FakeVehicleLookupConfig extends VehicleLookupConfig{
  override lazy val baseUrl = getOptionalProperty[String]("vehicleLookup.baseUrl").getOrElse("")

}
