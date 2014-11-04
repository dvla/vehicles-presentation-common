package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import play.api.libs.json.Json

final case class VehicleAndKeeperDetailsRequest(referenceNumber: String,
                                                registrationNumber: String)

object VehicleAndKeeperDetailsRequest {

  implicit val JsonFormat = Json.format[VehicleAndKeeperDetailsRequest]

  // TODO : Removing this will be a problem for to call it, need to have something equivalent in pr???
//  def from(vehicleAndKeeperLookupFormModel: VehicleAndKeeperLookupFormModel): VehicleAndKeeperDetailsRequest = {
//    VehicleAndKeeperDetailsRequest(
//      referenceNumber = vehicleAndKeeperLookupFormModel.referenceNumber,
//      registrationNumber = vehicleAndKeeperLookupFormModel.registrationNumber
//    )
//  }
}