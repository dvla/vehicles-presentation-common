package services.brute_force_prevention

import viewmodels.BruteForcePreventionViewModel

import scala.concurrent.Future

trait BruteForcePreventionService {
  def isVrmLookupPermitted(vrm: String): Future[BruteForcePreventionViewModel]
}
