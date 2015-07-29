package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

import scala.concurrent.Future

trait AcquireService {
  def invoke(cmd: AcquireRequestDto, trackingId: TrackingId): Future[(Int, Option[AcquireResponseDto])]
}
