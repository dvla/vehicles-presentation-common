package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.json.Json

case class VehicleAndKeeperLookupErrorMessage(code: String, message: String)

case class VehicleAndKeeperLookupResponseV2(responseCode: Option[VehicleAndKeeperLookupErrorMessage], vehicleAndKeeperDetailsDto: Option[VehicleAndKeeperDetailsDto])

object VehicleAndKeeperLookupResponseV2 {

  implicit val VehicleAndKeeperLookupErrorMessageJsonFormat = Json.format[VehicleAndKeeperLookupErrorMessage]
  implicit val VehicleAndKeeperLookupResponseV2JsonFormat = Json.format[VehicleAndKeeperLookupResponseV2]
}