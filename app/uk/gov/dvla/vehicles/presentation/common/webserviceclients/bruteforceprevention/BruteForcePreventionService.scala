package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel

trait BruteForcePreventionService {
  def isVrmLookupPermitted(vrm: String, trackingId: TrackingId): Future[BruteForcePreventionModel]
  def reset(vrm: String, trackingId: TrackingId): Future[Int]
}
