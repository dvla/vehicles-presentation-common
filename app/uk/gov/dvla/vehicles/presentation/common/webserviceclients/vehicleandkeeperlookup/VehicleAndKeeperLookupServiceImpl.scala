package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import javax.inject.Inject
import play.Logger
import play.api.http.Status
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

final class VehicleAndKeeperLookupServiceImpl @Inject()(ws: VehicleAndKeeperLookupWebService, healthStats: HealthStats)
  extends VehicleAndKeeperLookupService {
  import VehicleAndKeeperLookupServiceImpl.ServiceName

  override def invoke(cmd: VehicleAndKeeperDetailsRequest,
                      trackingId: String): Future[VehicleAndKeeperLookupResponseV2] =
    ws.invoke(cmd, trackingId).map { resp =>
      Logger.debug(s"Vehicle and keeper lookup service returned ${resp.status} code - trackingId: $trackingId")
      if (resp.status == Status.OK) {
        val response = resp.json.as[VehicleAndKeeperLookupResponseV2]

        // Horrible workaround to overcome the sophisticated way the errors are returned
        response.responseCode match {
          case Some(error) if error.code.startsWith("VMPR2") || error.code.startsWith("VMPR3") =>
            healthStats.failure(ServiceName, new Exception(s"${error.code} - ${error.message}"))
          case _ => healthStats.success(ServiceName)
        }
        response
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
