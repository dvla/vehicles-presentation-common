package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Form

case class NewKeeperChooseYourAddressViewModel(form: Form[NewKeeperChooseYourAddressFormModel],
                                               vehicleDetails: VehicleAndKeeperDetailsModel)