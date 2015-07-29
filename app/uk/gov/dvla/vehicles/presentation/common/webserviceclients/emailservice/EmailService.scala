package webserviceclients.emailservice

import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

import scala.concurrent.Future

trait EmailService {
  def invoke(cmd: EmailServiceSendRequest, trackingId: TrackingId): Future[EmailServiceSendResponse]
}