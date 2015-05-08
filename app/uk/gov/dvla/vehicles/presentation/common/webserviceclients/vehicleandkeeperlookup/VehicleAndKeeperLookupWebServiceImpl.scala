package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import com.google.inject.Inject
import play.api.Logger
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.{WS, WSResponse}
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import scala.concurrent.Future

final class VehicleAndKeeperLookupWebServiceImpl @Inject()(config: VehicleAndKeeperLookupConfig)
  extends VehicleAndKeeperLookupWebService {

  private val endPoint: String =
    s"${config.vehicleAndKeeperLookupMicroServiceBaseUrl}/vehicleandkeeper/lookup/v1"

  private val requestTimeout: Int = config.requestTimeout

  override def invoke(request: VehicleAndKeeperLookupRequest, trackingId: String): Future[WSResponse] = {
    val vrm = LogFormats.anonymize(request.registrationNumber)
    val refNo = LogFormats.anonymize(request.referenceNumber)

    Logger.debug(s"Calling vehicle and keeper lookup micro-service ($endPoint) with request $refNo $vrm tracking id: $trackingId")
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId)
      .withRequestTimeout(requestTimeout)
      .post(Json.toJson(request))
  }
}
