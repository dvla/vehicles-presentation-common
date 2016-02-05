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
      } else if (resp.status == Status.NOT_FOUND) {
        val msg = "Vehicle and keeper lookup web service failed to lookup vehicle. " +
          s"Returned with status: ${resp.status} body: ${parseResponseBody(resp.body)}"
        logMessage(trackingId, Debug, msg)
        if (resp.body.startsWith("The requested resource could not be found")) {
          // The 404 indicates that there is no resource listening on the url that was specified
          // so we report this as a failure to the healthStats service
          val e = new RuntimeException(
            "Vehicle and keeper lookup web service call http status not OK, it " +
              s"was status: ${resp.status} body: ${resp.body}"
          )
          healthStats.failure(ServiceName, e)
          throw e
        } else {
          // The 404 indicates a business failure so we report that as a success to the healthStats service
          healthStats.success(ServiceName)
          Left(resp.json.as[VehicleAndKeeperLookupFailureResponse])
        }
      } else {
        val e =  new RuntimeException(
          "Vehicle and keeper lookup web service call http status not OK, it " +
          s"was status: ${resp.status} body: ${resp.body}. Problem may come from either vehicle and keeper " +
          s"lookup micro-service or VPDS"
        )
        healthStats.failure(ServiceName, e)
        throw e
      }
    }.recover {
      case NonFatal(e) =>
        healthStats.failure(ServiceName, e)
        throw e
    }

  private def parseResponseBody(responseBody: String): String = {
    def isJsonResponse = responseBody.startsWith("{")

    val whiteSpaceAndCarriageReturnRegex = "[\\s\\r\\n]"
    if (isJsonResponse) responseBody.replaceAll(whiteSpaceAndCarriageReturnRegex, "") else responseBody
  }
}

object VehicleAndKeeperLookupServiceImpl {
  final val ServiceName = "vehicle-and-keeper-lookup-microservice"
}
