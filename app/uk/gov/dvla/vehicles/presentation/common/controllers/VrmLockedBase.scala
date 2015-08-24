package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller, Request, Result}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.model.{CacheKeyPrefix, BruteForcePreventionModel}

abstract class VrmLockedBase @Inject()()
                             (implicit protected val clientSideSessionFactory: ClientSideSessionFactory,
                              prefix: CacheKeyPrefix
                             ) extends Controller with DVLALogger {

  protected def presentResult(model: BruteForcePreventionModel)
                             (implicit request: Request[_]): Result

  protected def missingBruteForcePreventionCookie(implicit request: Request[_]): Result

  protected def tryAnotherResult(implicit request: Request[_]): Result

  protected def exitResult(implicit request: Request[_]): Result

  def present = Action { implicit request =>
    request.cookies.getModel[BruteForcePreventionModel] match {
      case Some(viewModel) =>
        logMessage(request.cookies.trackingId(), Debug, "VrmLocked - Displaying the vrm locked error page")
        presentResult(viewModel)
      case None =>
        logMessage(request.cookies.trackingId(), Debug, "VrmLocked - Can't find cookie for BruteForcePreventionViewModel")
        missingBruteForcePreventionCookie
    }
  }

  def tryAnother = Action { implicit request =>
    tryAnotherResult
  }

  def exit = Action { implicit request =>
    exitResult
  }
}
