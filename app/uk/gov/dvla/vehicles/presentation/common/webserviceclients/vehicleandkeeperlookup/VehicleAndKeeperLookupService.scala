package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

import scala.concurrent.Future

trait VehicleAndKeeperLookupService {

  def invoke(cmd: VehicleAndKeeperLookupRequest, trackingId: TrackingId): Future[VehicleAndKeeperLookupResponse]
}
