package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.libs.json.Reads
import play.api.mvc.{Action, Controller, DiscardingCookie, Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.LogFormats.DVLALogger
import common.model.BruteForcePreventionModel
import common.model.CacheKeyPrefix

abstract class VehicleLookupFailureBase[FormModel <: VehicleLookupFormModelBase]
  (implicit clientSideSessionFactory: ClientSideSessionFactory,
   fromJson: Reads[FormModel],
   cacheKey: CacheKey[FormModel],
   cacheKeyPrefix: CacheKeyPrefix
  ) extends Controller with DVLALogger {

  protected def presentResult(model: FormModel, responseCode: String)(implicit request: Request[_]): Result
  protected def missingPresentCookieDataResult()(implicit request: Request[_]): Result
  protected def submitResult()(implicit request: Request[_]): Result
  protected def missingSubmitCookieDataResult()(implicit request: Request[_]): Result
  protected val vehicleLookupResponseCodeCacheKey: String

  def present = Action { implicit request =>
    implicit val bruteForceCacheKey = BruteForcePreventionModel.key

    (request.cookies.getModel[BruteForcePreventionModel],
      request.cookies.getModel[FormModel],
      request.cookies.getString(vehicleLookupResponseCodeCacheKey)
      ) match {
      case (Some(bruteForcePreventionResponse),
            Some(vehicleLookUpFormModelDetails),
            Some(vehicleLookupResponseCode)) =>
        val responseCode = vehicleLookupResponseCode.split("-").map(_.trim)
        presentResult(vehicleLookUpFormModelDetails, responseCode.last).
          discardingCookies(DiscardingCookie(name = vehicleLookupResponseCodeCacheKey))
      case _ =>
        val msg = "VehicleLookupFailure present could not find all the cookie data. A redirection will now occur"
        logMessage(request.cookies.trackingId, Debug, msg)
        missingPresentCookieDataResult()
    }
  }

  def submit = Action { implicit request =>
    request.cookies.getModel[FormModel] match {
      case Some(vehicleLookUpFormModelDetails) =>
        logMessage(request.cookies.trackingId, Debug, "VehicleLookupFailure submit successfully found cookie data")
        submitResult()
      case _ =>
        val msg = "VehicleLookupFailure submit could not find all the cookie data. A redirection will now occur"
        logMessage(request.cookies.trackingId, Debug, msg)
        missingSubmitCookieDataResult()
    }
  }
}
