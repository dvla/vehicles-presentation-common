package uk.gov.dvla.vehicles.presentation.common.model

final case class DisposeModel(vehicleMake: String,
                              vehicleModel: String,
                              dealerName: String,
                              dealerAddress: AddressModel,
                              transactionId: Option[String] = None,
                              registrationNumber: String)
