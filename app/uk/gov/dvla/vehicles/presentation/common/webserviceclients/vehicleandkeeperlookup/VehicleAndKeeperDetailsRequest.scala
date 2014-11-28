package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import org.joda.time.DateTime
import play.api.libs.json.Json

final case class VehicleAndKeeperDetailsRequest(referenceNumber: String,
                                                registrationNumber: String,
                                                transactionTimestamp: DateTime)

object VehicleAndKeeperDetailsRequest {

  implicit val JsonFormat = Json.format[VehicleAndKeeperDetailsRequest]

}