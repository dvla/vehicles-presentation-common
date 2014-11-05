package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.Logger
import play.api.libs.json.Writes
import play.api.mvc.{Call, Controller, Request, Result}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.clientsidesession.{CacheKey, ClientSideSessionFactory}
import common.controllers.VehicleLookupBase.{LookupResult, VehicleFound, VehicleNotFound}
import common.LogFormats
import common.model.BruteForcePreventionModel
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService

trait VehicleLookupBase extends Controller {
  val vrmLocked: Call
  val vehicleLookupFailure: Call
  val microServiceError: Call
  val bruteForceService: BruteForcePreventionService
  val responseCodeCacheKey: String

  type Form

  implicit val clientSideSessionFactory: ClientSideSessionFactory

  def bruteForceAndLookup(registrationNumber: String, referenceNumber: String, form: Form)
                         (implicit request: Request[_], toJson: Writes[Form], cacheKey: CacheKey[Form]): Future[Result] =
    bruteForceService.isVrmLookupPermitted(registrationNumber).flatMap { bruteForcePreventionModel =>
      val resultFuture = if (bruteForcePreventionModel.permitted)
        lookupVehicle(registrationNumber, referenceNumber, bruteForcePreventionModel, form)
      else Future.successful {
        val anonRegistrationNumber = LogFormats.anonymize(registrationNumber)
        Logger.warn(s"BruteForceService locked out vrm: $anonRegistrationNumber")
        Redirect(vrmLocked)
      }

      resultFuture.map { result =>
        result.withCookie(bruteForcePreventionModel)
      }

    } recover {
      case exception: Throwable =>
        Logger.error(
          s"Exception thrown by BruteForceService so for safety we won't let anyone through. " +
            s"Exception ${exception.getStackTrace}"
        )
        Redirect(microServiceError)
    } map { result =>
      result.withCookie(form)
    }

  protected def callLookupService(trackingId: String, form: Form)(implicit request: Request[_]): Future[LookupResult]

  private def lookupVehicle(registrationNumber: String,
                            referenceNumber: String,
                            bruteForcePreventionModel: BruteForcePreventionModel,
                            form: Form)
                           (implicit request: Request[_]): Future[Result] = {
    def notFound(responseCode: String): Result = {
      Logger.debug(s"VehicleAndKeeperLookup encountered a problem with request" +
        s" ${LogFormats.anonymize(referenceNumber)}" +
        s" ${LogFormats.anonymize(registrationNumber)}," +
        s" redirect to VehicleAndKeeperLookupFailure")
      Redirect(vehicleLookupFailure).
        withCookie(responseCodeCacheKey, responseCode)
    }

    callLookupService(request.cookies.trackingId(), form).map {
      case VehicleNotFound(responseCode) => notFound(responseCode)
      case VehicleFound(result) =>
        bruteForceService.reset(registrationNumber).onComplete {
          case Success(httpCode) => Logger.debug(s"Brute force reset was called - it returned httpCode: $httpCode")
          case Failure(t) => Logger.error(s"Brute force reset failed: ${t.getStackTrace}")
        }
        result
    } recover {
      case NonFatal(e) =>
        microServiceErrorResult("Lookup web service call failed.", e)
    }
  }

  private def microServiceErrorResult(message: String, exception: Throwable): Result = {
    Logger.error(message, exception)
    Redirect(microServiceError)
  }
}

object VehicleLookupBase {
  sealed trait LookupResult
  
  final case class VehicleNotFound(responseCode: String) extends LookupResult

  final case class VehicleFound(result: Result) extends LookupResult
}
