package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.MicroserviceResponse

final case class VehicleAndKeeperLookupFailureResponse(response: MicroserviceResponse)

final case class VehicleAndKeeperLookupSuccessResponse(
                                                   vehicleAndKeeperDetailsDto: Option[VehicleAndKeeperLookupDetailsDto])

object VehicleAndKeeperLookupFailureResponse {

  implicit val VehicleAndKeeperLookupFailureResponseJsonFormat = Json.format[VehicleAndKeeperLookupFailureResponse]
}

object VehicleAndKeeperLookupSuccessResponse {

  implicit val VehicleAndKeeperLookupSuccessResponseJsonFormat = Json.format[VehicleAndKeeperLookupSuccessResponse]
}
