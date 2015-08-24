package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import play.api.libs.ws.WSResponse
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger

// Wrapper around our webservice call so that we can IoC fake versions for testing or use the real version.
trait BruteForcePreventionWebService extends DVLALogger {
  def callBruteForce(vrm: String, trackingId: TrackingId): Future[WSResponse]
  def reset(vrm: String, trackingId: TrackingId): Future[WSResponse]
}
