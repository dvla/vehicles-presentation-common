package uk.gov.dvla.vehicles.presentation.common.controllers

import org.scalatest.mock.MockitoSugar
import play.api.mvc.{Request, Result}
import scala.collection.mutable.ArrayBuffer
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel
import uk.gov.dvla.vehicles.presentation.common.model.CacheKeyPrefix

object VrmLockedTesting extends MockitoSugar {
  import play.api.mvc.Results.Ok

  val presentTestResult = Ok("presentResult")
  val missingBruteForcePreventionCookieTestResult = Ok("missingBruteForcePreventionCookieResult")
  val tryAnotherTestResult = Ok("tryAnotherResult")
  val exitTestResult = Ok("exitResult")
}

class VrmLockedTesting(implicit override val clientSideSessionFactory: ClientSideSessionFactory,
                       prefix: CacheKeyPrefix) extends VrmLockedBase {

  import VrmLockedTesting._

  val presentResultArgs = ArrayBuffer[BruteForcePreventionModel]()

  protected def presentResult(model: BruteForcePreventionModel)
                             (implicit request: Request[_]): Result = {
    presentResultArgs.append(model)
    presentTestResult
  }

  protected def missingBruteForcePreventionCookie(implicit request: Request[_]): Result =
    missingBruteForcePreventionCookieTestResult

  protected def tryAnotherResult(implicit request: Request[_]): Result = tryAnotherTestResult

  protected def exitResult(implicit request: Request[_]): Result = exitTestResult
}
