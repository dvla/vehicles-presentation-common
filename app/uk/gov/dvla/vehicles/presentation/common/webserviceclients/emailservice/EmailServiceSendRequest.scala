package webserviceclients.emailservice

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.{From, Attachment}

case class EmailServiceSendRequest(plainTextMessage: String,
                                   htmlMessage: String,
                                   attachment: Option[Attachment] = None,
                                   from: From,
                                   subject: String,
                                   toReceivers: Option[List[String]],
                                   ccReceivers: Option[List[String]])

object EmailServiceSendRequest {
  implicit val emailServiceSendRequestWrites = Json.writes[EmailServiceSendRequest]
}