package services.dispose_service

import models.domain.disposal_of_vehicle.{DisposeRequestDto, DisposeResponseDto}
import scala.concurrent.Future

trait DisposeService {
  def invoke(cmd: DisposeRequestDto, trackingId: String): Future[(Int, Option[DisposeResponseDto])]
}
