package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.libs.json.Writes
import play.api.mvc.{Action, Controller, Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{CacheKey, ClientSideSessionFactory, TrackingId}
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.controllers.VehicleLookupBase.{LookupResult, VehicleFound, VehicleNotFound}
import common.LogFormats.{DVLALogger, anonymize, optionNone}
import uk.gov.dvla.vehicles.presentation.common.model.{BruteForcePreventionModel, CacheKeyPrefix, MicroserviceResponseModel}
import common.services.DateService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import common.webserviceclients.common.DmsWebHeaderDto
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupDetailsDto
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupFailureResponse
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupRequest
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupService
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import common.views.models.DayMonthYear
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import common.views.constraints.Postcode.formatPostcode

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
 dateService: DateService) extends Controller with DVLALogger {


  def presentResult(implicit request: Request[_]): Result
  def microServiceError(t: Throwable, formModel: FormModel)(implicit request: Request[_]): Result
  def invalidFormResult(invalidForm: play.api.data.Form[FormModel])(implicit request: Request[_]): Future[Result]
  def vehicleLookupFailure(response: VehicleAndKeeperLookupFailureResponse, formModel: FormModel)
                          (implicit request: Request[_]): Result
  def vehicleFoundResult(vehicleAndKeeperDetailsDto: VehicleAndKeeperLookupDetailsDto,
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
                                 (implicit request: Request[_]): Future[Result] = {
    logMessage(request.cookies.trackingId(), Debug, s"VehicleLookupBase.bruteForceAndLookup entry")
    bruteForceService.isVrmLookupPermitted(formModel.registrationNumber, request.cookies.trackingId())
      .flatMap { bruteForcePreventionModel =>
      val resultFuture = if (bruteForcePreventionModel.permitted)
        lookupVehicle(formModel.registrationNumber, formModel.referenceNumber, bruteForcePreventionModel, formModel)
      else Future.successful {
        val anonRegistrationNumber = anonymize(formModel.registrationNumber)
        logMessage(request.cookies.trackingId(), Warn,s"BruteForceService locked out vrm: $anonRegistrationNumber")
        vrmLocked(bruteForcePreventionModel, formModel)
      }

      resultFuture.map { result =>
        import BruteForcePreventionModel.{key, JsonFormat}
        logMessage(request.cookies.trackingId(), Debug, s"VehicleLookupBase.bruteForceAndLookup exit")
        result.withCookie(bruteForcePreventionModel)(JsonFormat, key(cacheKeyPrefix), request, clientSideSessionFactory)
      }
    } recover {
      case exception: Throwable =>
        logMessage(request.cookies.trackingId(), Error,
          s"Exception thrown by BruteForceService so for safety we won't let anyone through. " +
            s"Exception:\n${exception.getMessage}\n${exception.getStackTrace}"
        )
        microServiceError(exception, formModel)
    } map (_.withCookie(formModel))
  }

  private def lookupVehicle(registrationNumber: String,
                            referenceNumber: String,
                            bruteForcePreventionModel: BruteForcePreventionModel,
                            formModel: FormModel)
                           (implicit request: Request[_]): Future[Result] = {
    def notFound(failure: VehicleAndKeeperLookupFailureResponse): Result = {
      logMessage(request.cookies.trackingId(), Debug, "VehicleAndKeeperLookup encountered a problem with request" +
        s" ${anonymize(referenceNumber)}" +
        s" ${anonymize(registrationNumber)}," +
        " redirect to VehicleAndKeeperLookupFailure")
      vehicleLookupFailure(failure, formModel).withCookie(MicroserviceResponseModel.content(failure.response))
    }

    logMessage(request.cookies.trackingId(), Debug, s"VehicleLookupBase.lookupVehicle entry")
    callLookupService(request.cookies.trackingId(), formModel).map {
      case VehicleNotFound(failure) => notFound(failure)
      case VehicleFound(result) =>
          logMessage(request.cookies.trackingId(), Debug, s"VehicleLookupBase.lookupVehicle - vehicle found")
          bruteForceService.reset(registrationNumber, request.cookies.trackingId()).onComplete {
          case Success(httpCode) =>
            val msg = s"Brute force reset was called - it returned httpCode: $httpCode"
            logMessage(request.cookies.trackingId(), Debug, msg)
          case Failure(t) =>
            val msg = s"Brute force reset failed: ${t.getStackTrace} "
            logMessage(request.cookies.trackingId(), Error, msg)
        }
        result
    } recover {
      case NonFatal(e) => microServiceErrorResult("Vehicle lookup web service call failed.", e, formModel)
    }
  }

  protected def callLookupService(trackingId: TrackingId, formModel: FormModel)
                                 (implicit request: Request[_]): Future[LookupResult] = {

    val vehicleAndKeeperDetailsRequest = VehicleAndKeeperLookupRequest(
      dmsHeader = buildHeader(trackingId),
      referenceNumber = formModel.referenceNumber,
      registrationNumber = formModel.registrationNumber,
      transactionTimestamp = dateService.now.toDateTime
    )

    logMessage( trackingId, Debug, "Vehicle lookup web service request",
      Some(Seq(vehicleAndKeeperDetailsRequest.dmsHeader.applicationCode,
      vehicleAndKeeperDetailsRequest.dmsHeader.channelCode,
      vehicleAndKeeperDetailsRequest.dmsHeader.contactId.toString,
      vehicleAndKeeperDetailsRequest.dmsHeader.conversationId,
      vehicleAndKeeperDetailsRequest.dmsHeader.eventFlag.toString,
      vehicleAndKeeperDetailsRequest.dmsHeader.languageCode,
      vehicleAndKeeperDetailsRequest.dmsHeader.originDateTime.toString,
      vehicleAndKeeperDetailsRequest.dmsHeader.serviceTypeCode,
      anonymize(vehicleAndKeeperDetailsRequest.referenceNumber),
      anonymize(vehicleAndKeeperDetailsRequest.registrationNumber),
      vehicleAndKeeperDetailsRequest.transactionTimestamp.toString)) )

    vehicleLookupService.invoke(vehicleAndKeeperDetailsRequest, trackingId) map { response =>
      response match {
        case Left(error) =>
          VehicleNotFound(error)
        case Right(success) =>
          success.vehicleAndKeeperDetailsDto match {
            case Some(dto) =>
              logMessage( trackingId, Debug, "Vehicle lookup web service response",
                Some(Seq(anonymize(dto.registrationNumber),
                  dto.vehicleMake.getOrElse(optionNone),
                  dto.vehicleModel.getOrElse(optionNone),
                  dto.keeperTitle.getOrElse(optionNone),
                  anonymize(dto.keeperFirstName),
                  anonymize(dto.keeperLastName),
                  anonymize(dto.keeperAddressLine1),
                  anonymize(dto.keeperAddressLine2),
                  anonymize(dto.keeperAddressLine3),
                  anonymize(dto.keeperAddressLine4),
                  anonymize(dto.keeperPostTown),
                  anonymize(dto.keeperPostcode),
                  dto.disposeFlag.getOrElse(optionNone).toString,
                  dto.keeperEndDate.getOrElse(optionNone).toString,
                  dto.keeperChangeDate.getOrElse(optionNone).toString,
                  dto.suppressedV5Flag.getOrElse(optionNone).toString)) )
              VehicleFound(vehicleFoundResult(dto, formModel))
            case None => throw new RuntimeException("No vehicleDetailsDto found")
          }
      }
    }
  }

  private def microServiceErrorResult(message: String, exception: Throwable, formModel: FormModel)
                                     (implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(),Error, message)
    logMessage(request.cookies.trackingId(),Error, s"Failure was because: ${exception.getMessage}")
    microServiceError(exception, formModel)
  }

  private def buildHeader(trackingId: TrackingId): DmsWebHeaderDto = {
    val alwaysLog = true
    val englishLanguage = "EN"
    DmsWebHeaderDto(conversationId = trackingId.value,
      originDateTime = dateService.now.toDateTime,
      applicationCode = config.applicationCode,
      channelCode = config.channelCode,
      contactId = config.contactId,
      eventFlag = alwaysLog,
      serviceTypeCode = config.dmsServiceTypeCode,
      languageCode = englishLanguage,
      endUser = None)
  }

  // payment solve requires (for each day) a unique six digit number
  // use time from midnight in tenths of a second units
  private def calculatePaymentTransNo = {
    val milliSecondsFromMidnight = dateService.today.toDateTime.get.millisOfDay().get()
    val tenthSecondsFromMidnight = (milliSecondsFromMidnight / 100.0).toInt
    // prepend with zeros
    "%06d".format(tenthSecondsFromMidnight)
  }

  protected def addDefaultCookies(result: Result, transactionId: String, txnIdCacheKey: String, paymentTxnNoCacheKey: String)
                               (implicit request: Request[_]): Result = result
    .withCookie(txnIdCacheKey, transactionId)
    .withCookie(paymentTxnNoCacheKey, calculatePaymentTransNo)

  protected def transactionId(registrationNumber: String): String = {
      val transactionTimestamp =
        DayMonthYear.from(new DateTime(dateService.now, DateTimeZone.forID("Europe/London"))).toDateTimeMillis.get
      val isoDateTimeString = ISODateTimeFormat.yearMonthDay().print(transactionTimestamp).drop(2) + " " +
        ISODateTimeFormat.hourMinuteSecond().print(transactionTimestamp)
      registrationNumber +
        isoDateTimeString.replace(" ", "").replace("-", "").replace(":", "").replace(".", "")
    }

  protected def postcodesMatch(formModelPostcode: String, dtoPostcode: Option[String])
                              (trackingId: TrackingId) = {
      dtoPostcode match {
        case Some(postcode) =>
          val msg = s"formModelPostcode = ${LogFormats.anonymize(formModelPostcode)}, " +
          s"dtoPostcode = ${LogFormats.anonymize(postcode)}"
          logMessage(trackingId, Info, msg)

          def formatPartialPostcode(postcode: String): String = {
            val SpaceCharDelimiter = " "
            val A99AA = "([A-Z][0-9][*]{3})".r
            val A099AA = "([A-Z][0][0-9][*]{3})".r
            val A999AA = "([A-Z][0-9]{2}[*]{3})".r
            val A9A9AA = "([A-Z][0-9][A-Z][*]{3})".r
            val AA99AA = "([A-Z]{2}[0-9][*]{3})".r
            val AA099AA = "([A-Z]{2}[0][0-9][*]{3})".r
            val AA999AA = "([A-Z]{2}[0-9]{2}[*]{3})".r
            val AA9A9AA = "([A-Z]{2}[0-9][A-Z][*]{3})".r

            postcode.toUpperCase.replace(SpaceCharDelimiter, "") match {
              case A99AA(p) => p.substring(0, 2)
              case A099AA(p) => p.substring(0, 1) + p.substring(2, 3)
              case A999AA(p) => p.substring(0, 3)
              case A9A9AA(p) => p.substring(0, 3)
              case AA99AA(p) => p.substring(0, 3)
              case AA099AA(p) => p.substring(0, 2) + p.substring(3, 4)
              case AA999AA(p) => p.substring(0, 4)
              case AA9A9AA(p) => p.substring(0, 4)
              case _ => formatPostcode(postcode)
            }
          }

          // strip the spaces before comparison
          formatPostcode(formModelPostcode).filterNot(" " contains _).toUpperCase ==
            formatPartialPostcode(postcode).filterNot(" " contains _).toUpperCase

        case None =>
          logMessage(trackingId, Info, s"formModelPostcode = ${LogFormats.anonymize(formModelPostcode)}")
          formModelPostcode.isEmpty
      }
    }

}

object VehicleLookupBase {
  sealed trait LookupResult

  final case class VehicleNotFound(failure: VehicleAndKeeperLookupFailureResponse) extends LookupResult

  final case class VehicleFound(result: Result) extends LookupResult

  final val RESPONSE_CODE_DELIMITER = " - "
  // ms response codes (correlate to the name of a template html file in views.<exemplar>.lookup_failure)
  final val RESPONSE_CODE_VRM_LOCKED = "vrm_locked"
  final val RESPONSE_CODE_POSTCODE_MISMATCH = "vehicle_and_keeper_lookup_keeper_postcode_mismatch"
  // exemplar failure codes
  final val FAILURE_CODE_VKL_UNHANDLED_EXCEPTION = "VMPR6"
}
