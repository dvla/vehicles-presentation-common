package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import javax.inject.Inject
import play.api.http.Status
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

final class EmailServiceImpl @Inject()(ws: EmailServiceWebService) extends EmailService {

  override def invoke(cmd: EmailServiceSendRequest, trackingId: TrackingId): Future[EmailServiceSendResponse] = {
    ws.invoke(cmd, trackingId).map { resp =>
      if (resp.status == Status.OK) new EmailServiceSendResponse
      else throw new RuntimeException(
        s"Email Service web service call http status not OK, it " +
          s"was: ${resp.status}. Problem may come from either email-service micro-service or the email server"
      )
    }.recover {
      case NonFatal(e) => throw new RuntimeException("Email Service call failed for an unknown reason", e)
    }
  }

}