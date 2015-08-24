package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

trait EmailService {
  def invoke(cmd: EmailServiceSendRequest, trackingId: TrackingId): Future[EmailServiceSendResponse]
}