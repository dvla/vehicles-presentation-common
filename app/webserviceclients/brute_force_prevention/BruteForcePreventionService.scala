package webserviceclients.brute_force_prevention

import models.BruteForcePreventionModel

import scala.concurrent.Future

trait BruteForcePreventionService {
  def isVrmLookupPermitted(vrm: String): Future[BruteForcePreventionModel]
}
