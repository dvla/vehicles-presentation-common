package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

trait VehicleAndKeeperLookupService {

  def invoke(cmd: VehicleAndKeeperLookupRequest, trackingId: TrackingId): Future[VehicleAndKeeperLookupResponse]
}
