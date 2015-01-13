package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup

import javax.inject.Inject
import play.api.Logger
import play.api.http.Status
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

final class VehicleLookupServiceImpl @Inject()(ws: VehicleLookupWebService) extends VehicleLookupService {
  override def invoke(cmd: VehicleDetailsRequestDto,
                      trackingId: String): Future[VehicleDetailsResponseDto] =
    ws.callVehicleLookupService(cmd, trackingId).map { resp =>
      Logger.debug(s"Vehicle lookup service returned ${resp.status} code")
      if (resp.status == Status.OK)
        resp.json.as[VehicleDetailsResponseDto]
      else throw new RuntimeException(
        s"Vehicle lookup web service call http status not OK, it " +
        s"was: ${resp.status}: ${resp.body}. Problem may come from either vehicle lookup micro-service or the VSS"
      )
    }.recover {
      case NonFatal(e) =>
        Logger.error(e.getStackTraceString)
        throw new RuntimeException("Vehicle lookup call failed for an unknown reason", e)
    }
}
