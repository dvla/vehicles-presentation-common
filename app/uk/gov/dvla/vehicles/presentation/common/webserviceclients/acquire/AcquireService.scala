package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

trait AcquireService {
  def invoke(cmd: AcquireRequestDto, trackingId: TrackingId): Future[(Int, Option[AcquireResponseDto])]
}
