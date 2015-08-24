package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

import scala.concurrent.Future

trait EmailServiceWebService {
  def invoke(request: EmailServiceSendRequest, trackingId: TrackingId): Future[WSResponse]
}