package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import scala.concurrent.Future

trait VehicleAndKeeperLookupService {

  def invoke(cmd: VehicleAndKeeperLookupRequest, trackingId: String): Future[VehicleAndKeeperLookupResponse]
}
