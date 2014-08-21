package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup

import javax.inject.Inject
import play.api.Logger
import play.api.http.Status
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class VehicleLookupServiceImpl @Inject()(ws: VehicleLookupWebService) extends VehicleLookupService {
  override def invoke(cmd: VehicleDetailsRequestDto,
                      trackingId: String): (Future[(Int, Option[VehicleDetailsResponseDto])]) =
    ws.callVehicleLookupService(cmd, trackingId).map { resp =>
      Logger.debug(s"Http response code from vehicle lookup micro-service was: ${resp.status}")
      if (resp.status == Status.OK) (resp.status, Some(resp.json.as[VehicleDetailsResponseDto]))
      else (resp.status, None)
    }
}
