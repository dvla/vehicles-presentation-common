package webserviceclients.emailservice

import scala.concurrent.Future

trait EmailService {
  def invoke(cmd: EmailServiceSendRequest, trackingId: String): Future[EmailServiceSendResponse]
}