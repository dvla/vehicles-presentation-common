package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.libs.json.Writes
import play.api.Logger
import play.api.mvc.{Action, Controller, Result, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{CacheKey, ClientSideSessionFactory}
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.controllers.VehicleLookupBase.{LookupResult, VehicleFound, VehicleNotFound}
import common.LogFormats
import common.model.{CacheKeyPrefix, BruteForcePreventionModel}
import common.services.DateService
import common.webserviceclients.common.DmsWebHeaderDto
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsDto
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsRequest
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService

trait VehicleLookupFormModelBase {
  val referenceNumber: String
  val registrationNumber: String
}

trait VehicleLookupConfig {
  def applicationCode: String
  def vssServiceTypeCode: String
  def dmsServiceTypeCode: String
  def orgBusinessUnit: String
  def channelCode: String
  def contactId: Long
}

abstract class VehicleLookupBase[FormModel <: VehicleLookupFormModelBase]
(implicit vehicleLookupService: VehicleAndKeeperLookupService,
 config: VehicleLookupConfig,
 bruteForceService: BruteForcePreventionService,
 clientSideSessionFactory: ClientSideSessionFactory,
 toJson: Writes[FormModel],
 cacheKey: CacheKey[FormModel],
 cacheKeyPrefix: CacheKeyPrefix,
 dateService: DateService) extends Controller {

  def presentResult(implicit request: Request[_]): Result
  def microServiceError(t: Throwable, formModel: FormModel)(implicit request: Request[_]): Result
  def invalidFormResult(invalidForm: play.api.data.Form[FormModel])(implicit request: Request[_]): Future[Result]
  def vehicleLookupFailure(responseCode: String, formModel: FormModel)(implicit request: Request[_]): Result
  def vehicleFoundResult(vehicleAndKeeperDetailsDto: VehicleAndKeeperDetailsDto,
                         validFormModel: FormModel)(implicit request: Request[_]): Result
  def vrmLocked(bruteForcePreventionModel: BruteForcePreventionModel, formModel: FormModel)
               (implicit request: Request[_]): Result

  val responseCodeCacheKey: String
  val form: play.api.data.Form[FormModel]

  def present = Action { implicit request => presentResult}

  def submit = Action.async { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => invalidFormResult(invalidForm),
      validFormModel => bruteForceAndLookup(validFormModel)
    )
  }

  private def bruteForceAndLookup(formModel: FormModel)
                                 (implicit request: Request[_]): Future[Result] =
    bruteForceService.isVrmLookupPermitted(formModel.registrationNumber).flatMap { bruteForcePreventionModel =>
      val resultFuture = if (bruteForcePreventionModel.permitted)
        lookupVehicle(formModel.registrationNumber, formModel.referenceNumber, bruteForcePreventionModel, formModel)
      else Future.successful {
        val anonRegistrationNumber = LogFormats.anonymize(formModel.registrationNumber)
        Logger.warn(s"BruteForceService locked out vrm: $anonRegistrationNumber - trackingId: ${request.cookies.trackingId()}")
        vrmLocked(bruteForcePreventionModel, formModel)
      }

      resultFuture.map { result =>
        import BruteForcePreventionModel.{key, JsonFormat}
        result.withCookie(bruteForcePreventionModel)(JsonFormat, key(cacheKeyPrefix), request, clientSideSessionFactory)
      }
    } recover {
      case exception: Throwable =>
        Logger.error(
          s"Exception thrown by BruteForceService so for safety we won't let anyone through. " +
            s"Exception:\n${exception.getMessage}\n${exception.getStackTraceString} - trackingId: ${request.cookies.trackingId()}"
        )
        microServiceError(exception, formModel)
    } map (_.withCookie(formModel))

  private def lookupVehicle(registrationNumber: String,
                            referenceNumber: String,
                            bruteForcePreventionModel: BruteForcePreventionModel,
                            formModel: FormModel)
                           (implicit request: Request[_]): Future[Result] = {
    def notFound(responseCode: String): Result = {
      Logger.debug(s"VehicleAndKeeperLookup encountered a problem with request" +
        s" ${LogFormats.anonymize(referenceNumber)}" +
        s" ${LogFormats.anonymize(registrationNumber)}," +
        s" redirect to VehicleAndKeeperLookupFailure - trackingId: ${request.cookies.trackingId()}")
      vehicleLookupFailure(responseCode, formModel).withCookie(responseCodeCacheKey, responseCode.split(" - ").last)
    }

    callLookupService(request.cookies.trackingId(), formModel).map {
      case VehicleNotFound(responseCode) => notFound(responseCode)
      case VehicleFound(result) =>
        bruteForceService.reset(registrationNumber).onComplete {
          case Success(httpCode) => Logger.debug(s"Brute force reset was called - it returned httpCode: $httpCode " +
            s"- trackingId: ${request.cookies.trackingId()}")
          case Failure(t) => Logger.error(s"Brute force reset failed: ${t.getStackTraceString} " +
            s"- trackingId: ${request.cookies.trackingId()}")
        }
        result
    } recover {
      case NonFatal(e) => microServiceErrorResult("Lookup web service call failed.", e, formModel)
    }
  }

  protected def callLookupService(trackingId: String, formModel: FormModel)
                                 (implicit request: Request[_]): Future[LookupResult] = {
    val vehicleAndKeeperDetailsRequest = VehicleAndKeeperDetailsRequest(
      dmsHeader = buildHeader(trackingId),
      referenceNumber = formModel.referenceNumber,
      registrationNumber = formModel.registrationNumber,
      transactionTimestamp =dateService.now.toDateTime
    )

    vehicleLookupService.invoke(vehicleAndKeeperDetailsRequest, trackingId) map { response =>
      response.responseCode match {
        case Some(error) =>
          VehicleNotFound(s"${error.code} - ${error.message}")
        case None =>
          response.vehicleAndKeeperDetailsDto match {
            case Some(dto) => VehicleFound(vehicleFoundResult(dto, formModel))
            case None => throw new RuntimeException("No vehicleDetailsDto found")
          }
      }
    }
  }

  private def microServiceErrorResult(message: String, exception: Throwable, formModel: FormModel)
                                     (implicit request: Request[_]): Result = {
    Logger.error(message, exception)
    microServiceError(exception, formModel)
  }

  private def buildHeader(trackingId: String): DmsWebHeaderDto = {
    val alwaysLog = true
    val englishLanguage = "EN"
    DmsWebHeaderDto(conversationId = trackingId,
      originDateTime = dateService.now.toDateTime,
      applicationCode = config.applicationCode,
      channelCode = config.channelCode,
      contactId = config.contactId,
      eventFlag = alwaysLog,
      serviceTypeCode = config.dmsServiceTypeCode,
      languageCode = englishLanguage,
      endUser = None)
  }
}

object VehicleLookupBase {
  sealed trait LookupResult

  final case class VehicleNotFound(responseCode: String) extends LookupResult

  final case class VehicleFound(result: Result) extends LookupResult
}
