package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.ws.WSResponse
import scala.concurrent.Future

trait VehicleAndKeeperLookupWebService {

  def invoke(request: VehicleAndKeeperLookupRequest, trackingId: String): Future[WSResponse]
}
