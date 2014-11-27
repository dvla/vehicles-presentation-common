package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel
import uk.gov.dvla.vehicles.presentation.common.services.DateService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class BruteForcePreventionServiceImpl @Inject()(config: BruteForcePreventionConfig,
                                                      ws: BruteForcePreventionWebService,
                                                      dateService: DateService) extends BruteForcePreventionService {

  private val maxAttempts: Int = config.maxAttemptsHeader.toInt

  override def isVrmLookupPermitted(vrm: String): Future[BruteForcePreventionModel] =
  // Feature toggle until all developers have Redis and the brute force micro service setup locally.
    if (config.isEnabled) {
      val returnedFuture = scala.concurrent.Promise[BruteForcePreventionModel]()
      ws.callBruteForce(vrm).map { resp =>
        def permitted(): Unit = {
          Json.fromJson[BruteForcePreventionResponseDto](resp.json).asOpt match {
            case Some(model) =>
              val resultModel = BruteForcePreventionModel.fromResponse(
                permitted = true,
                model,
                dateService,
                maxAttempts
              )
              returnedFuture.success(resultModel)
            case _ =>
              Logger.error(s"Brute force prevention service returned invalid Json: ${resp.json}")
              returnedFuture.failure(new Exception("TODO"))
          }
        }
        def notPermitted = BruteForcePreventionModel.fromResponse(permitted = false,
          BruteForcePreventionResponseDto(attempts = 0),
          dateService,
          maxAttempts = maxAttempts
        )
        resp.status match {
          case play.api.http.Status.OK => permitted()
          case play.api.http.Status.FORBIDDEN => returnedFuture.success(notPermitted)
          case _ => returnedFuture.failure(new Exception(s"unknownPermission '${resp.status}: ${resp.body}'"))
        }
      }.recover {
        case e: Throwable =>
          Logger.error(s"Brute force prevention service throw exception: ${e.getStackTraceString}")
          returnedFuture.failure(e)
      }
      returnedFuture.future
    }
    else Future {
      // This code is only reached when brute force prevention is disabled, which should only be when we are running
      // locally (i.e. sbt sandbox or sbt run) and NEVER in production.
      val vrmAttempt2 = "ST05YYB"
      val vrmLocked = "ST05YYC"
      vrm match {
        case `vrmAttempt2` =>
          // Block access
          BruteForcePreventionModel.fromResponse(permitted = false,
            BruteForcePreventionResponseDto(attempts = maxAttempts),
            dateService,
            maxAttempts = maxAttempts
          )
        case `vrmLocked` =>
          // Block access with warning
          BruteForcePreventionModel.fromResponse(permitted = false,
            BruteForcePreventionResponseDto(attempts = maxAttempts - 1),
            dateService,
            maxAttempts = maxAttempts
          )
        case _ =>
          // Allow access
          BruteForcePreventionModel.fromResponse(permitted = true,
            BruteForcePreventionResponseDto(attempts = 0),
            dateService,
            maxAttempts = maxAttempts
          )
      }
    }

  override def reset(vrm: String): Future[Int] =
  // Feature toggle until all developers have Redis and the brute force micro service setup locally.
    if (config.isEnabled) {
      val returnedFuture = scala.concurrent.Promise[Int]()
      ws.reset(vrm).map { resp =>
        returnedFuture.success(resp.status)
      }.recover { case e: Throwable =>
        returnedFuture.failure(e)
      }
      returnedFuture.future
    }
    else Future {
      play.api.http.Status.OK
    }
}
