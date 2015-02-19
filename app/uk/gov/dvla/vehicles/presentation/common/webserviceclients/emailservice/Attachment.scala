package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import play.api.libs.json.Json

case class Attachment(bytes: String, contentType: String, filename: String, description: String)

object Attachment {
  implicit val attachmentWrites = Json.writes[Attachment]
}