package webserviceclients.dispose_service

import scala.concurrent.Future

trait DisposeService {
  def invoke(cmd: DisposeRequestDto, trackingId: String): Future[(Int, Option[DisposeResponseDto])]
}
