package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel
import scala.concurrent.Future

trait BruteForcePreventionService {
  def isVrmLookupPermitted(vrm: String): Future[BruteForcePreventionModel]
}
