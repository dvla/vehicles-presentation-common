package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup

import play.api.libs.ws.Response
import scala.concurrent.Future

trait VehicleLookupWebService {
  def callVehicleLookupService(request: VehicleDetailsRequestDto, trackingId: String): Future[Response]
}
