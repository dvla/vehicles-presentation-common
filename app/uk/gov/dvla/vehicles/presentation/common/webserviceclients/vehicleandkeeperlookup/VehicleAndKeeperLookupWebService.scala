package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.ws.WSResponse
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

trait VehicleAndKeeperLookupWebService {

  def invoke(request: VehicleAndKeeperLookupRequest, trackingId: TrackingId): Future[WSResponse]
}
