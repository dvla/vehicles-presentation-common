package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Form

case class NewKeeperEnterAddressManuallyViewModel(form: Form[NewKeeperEnterAddressManuallyFormModel],
                                                  vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel)