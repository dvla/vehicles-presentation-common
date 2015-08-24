package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.{WS, WSResponse}
import play.api.Play.current
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders

final class VehicleAndKeeperLookupWebServiceImpl @Inject()(config: VehicleAndKeeperLookupConfig)
  extends VehicleAndKeeperLookupWebService with DVLALogger {

  private val endPoint: String =
    s"${config.vehicleAndKeeperLookupMicroServiceBaseUrl}/vehicleandkeeper/lookup/v1"

  private val requestTimeout: Int = config.requestTimeout

  override def invoke(request: VehicleAndKeeperLookupRequest, trackingId: TrackingId): Future[WSResponse] = {
    val vrm = LogFormats.anonymize(request.registrationNumber)
    val refNo = LogFormats.anonymize(request.referenceNumber)

    logMessage(trackingId, Debug, s"Calling vehicle and keeper lookup micro-service ($endPoint) with request $refNo $vrm")
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId.value)
      .withRequestTimeout(requestTimeout)
      .post(Json.toJson(request))
  }
}
