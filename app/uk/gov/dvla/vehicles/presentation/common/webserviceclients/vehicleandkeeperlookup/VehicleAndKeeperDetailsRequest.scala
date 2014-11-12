package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.json.Json

final case class VehicleAndKeeperDetailsRequest(referenceNumber: String,
                                                registrationNumber: String)

object VehicleAndKeeperDetailsRequest {

  implicit val JsonFormat = Json.format[VehicleAndKeeperDetailsRequest]

}