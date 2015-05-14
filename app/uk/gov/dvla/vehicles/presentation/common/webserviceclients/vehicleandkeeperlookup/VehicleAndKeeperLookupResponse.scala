package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.json.Json

case class VehicleAndKeeperLookupErrorMessage(code: String, message: String)

case class VehicleAndKeeperLookupResponse(responseCode: Option[VehicleAndKeeperLookupErrorMessage],
                                          vehicleAndKeeperDetailsDto: Option[VehicleAndKeeperLookupDetailsDto])

object VehicleAndKeeperLookupResponse {

  implicit val VehicleAndKeeperLookupErrorMessageJsonFormat = Json.format[VehicleAndKeeperLookupErrorMessage]
  implicit val VehicleAndKeeperLookupResponseJsonFormat = Json.format[VehicleAndKeeperLookupResponse]
}