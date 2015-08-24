package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import play.api.libs.ws.WSResponse
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

// TODO Do we still need this abstraction, now the code base is more mockable?
trait AcquireWebService {
  def callAcquireService(request: AcquireRequestDto, trackingId: TrackingId): Future[WSResponse]
}
