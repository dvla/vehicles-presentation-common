package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import play.api.libs.json.Json

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