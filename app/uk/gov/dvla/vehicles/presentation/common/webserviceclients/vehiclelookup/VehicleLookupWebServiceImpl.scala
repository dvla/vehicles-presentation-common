package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.{WSResponse, WS}
import play.api.Play.current
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.VehicleLookupConfig
import scala.concurrent.Future

final class VehicleLookupWebServiceImpl @Inject()(config: VehicleLookupConfig) extends VehicleLookupWebService {
  private val endPoint: String = s"${config.baseUrl}/vehicles/lookup/v1/dispose"

  override def callVehicleLookupService(request: VehicleDetailsRequestDto, trackingId: String): Future[WSResponse] = {
    val vrm = LogFormats.anonymize(request.registrationNumber)
    val refNo = LogFormats.anonymize(request.referenceNumber)

    Logger.debug(s"Calling vehicle lookup micro-service with request $refNo $vrm")
    Logger.debug(s"Calling vehicle lookup micro-service with tracking id: $trackingId")
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId)
      .post(Json.toJson(request))
  }
}
