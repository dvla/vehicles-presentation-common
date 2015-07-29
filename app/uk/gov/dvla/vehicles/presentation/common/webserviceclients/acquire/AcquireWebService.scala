package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import scala.concurrent.Future

// TODO Do we still need this abstraction, now the code base is more mockable?
trait AcquireWebService {
  def callAcquireService(request: AcquireRequestDto, trackingId: TrackingId): Future[WSResponse]
}
