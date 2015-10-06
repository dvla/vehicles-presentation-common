package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import javax.inject.Inject
import play.api.http.Status
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats

final class VehicleAndKeeperLookupServiceImpl @Inject()(ws: VehicleAndKeeperLookupWebService, healthStats: HealthStats)
  extends VehicleAndKeeperLookupService with DVLALogger {
  import VehicleAndKeeperLookupServiceImpl.ServiceName

  override def invoke(cmd: VehicleAndKeeperLookupRequest,
                      trackingId: TrackingId):
                      Future[Either[VehicleAndKeeperLookupFailureResponse, VehicleAndKeeperLookupSuccessResponse]] =
    ws.invoke(cmd, trackingId).map { resp =>
      logMessage(trackingId, Debug, s"Vehicle and keeper lookup service returned ${resp.status} code")
      if (resp.status == Status.OK) {
        healthStats.success(ServiceName)
        Right(resp.json.as[VehicleAndKeeperLookupSuccessResponse])
      } else if (resp.status == Status.INTERNAL_SERVER_ERROR) {
        val e =  new RuntimeException(
          s"Vehicle and keeper lookup web service call failed with ${resp.status}. " +
          s"body: ${resp.body}'. Problem may come from either vehicle and keeper " +
          s"lookup micro-service or the VPDS - trackingId: $trackingId"
        )
        healthStats.failure(ServiceName, e)
        Left(resp.json.as[VehicleAndKeeperLookupFailureResponse])
      } else {
        val e =  new RuntimeException(
          s"Vehicle and keeper lookup web service call http status not OK, it " +
          s"was: '${resp.status} body: ${resp.body}'. Problem may come from either vehicle and keeper " +
          s"lookup micro-service or the VPDS - trackingId: $trackingId"
        )
        healthStats.failure(ServiceName, e)
        throw e
      }
    }.recover {
      case NonFatal(e) =>
        healthStats.failure(ServiceName, e)
        throw new RuntimeException("Vehicle and keeper lookup call failed for an unknown " +
          s"reason - trackingId: $trackingId", e)
    }
}

object VehicleAndKeeperLookupServiceImpl {
  final val ServiceName = "vehicle-and-keeper-lookup-microservice"
}
