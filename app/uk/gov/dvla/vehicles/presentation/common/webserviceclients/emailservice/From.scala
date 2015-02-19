package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import play.api.libs.json.Json

case class From(email: String, name: String)

object From {
  implicit val fromWrites = Json.writes[From]
}