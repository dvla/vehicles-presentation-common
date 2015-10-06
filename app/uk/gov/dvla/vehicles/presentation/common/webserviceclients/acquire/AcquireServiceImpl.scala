package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import javax.inject.Inject
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats

final class AcquireServiceImpl @Inject()(config: AcquireConfig,
                                         ws: AcquireWebService,
                                         healthStats: HealthStats) extends AcquireService with DVLALogger {

  override def invoke(cmd: AcquireRequestDto, trackingId: TrackingId): Future[(Int, Option[AcquireResponseDto])] = {
    val vrm = LogFormats.anonymize(cmd.registrationNumber)
    val refNo = LogFormats.anonymize(cmd.referenceNumber)
    val postcode = cmd.traderDetails match {
      case Some(traderDetails) => LogFormats.anonymize(traderDetails.traderPostCode)
      case _ => ""
    }

    logMessage(trackingId, Debug, "Calling acquire vehicle micro-service with " +
      s"$refNo $vrm $postcode ${cmd.keeperConsent} ${cmd.keeperConsent} ${cmd.mileage}")

    healthStats.report("acquire-fulfil-microservice") {
      ws.callAcquireService(cmd, trackingId).map { resp =>
        val msg = s"Http response code from acquire vehicle micro-service was: ${resp.status}"
        logMessage(trackingId, Debug, msg)

        if (resp.status == OK)
          (resp.status, resp.json.asOpt[AcquireResponseDto])
        else if (resp.status == INTERNAL_SERVER_ERROR)
          (resp.status, resp.json.asOpt[AcquireResponseDto])
        else (resp.status, None)
      }
    }
  }
}
