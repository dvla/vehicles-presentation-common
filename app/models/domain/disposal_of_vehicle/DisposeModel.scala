package models.domain.disposal_of_vehicle

import viewmodels.AddressViewModel

final case class DisposeModel(vehicleMake: String,
                                  vehicleModel: String,
                                  dealerName: String,
                                  dealerAddress: AddressViewModel,
                                  transactionId: Option[String] = None,
                                  registrationNumber: String)
