package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import scala.concurrent.Future

trait VehicleAndKeeperLookupWebService {

  def invoke(request: VehicleAndKeeperLookupRequest, trackingId: TrackingId): Future[WSResponse]
}
