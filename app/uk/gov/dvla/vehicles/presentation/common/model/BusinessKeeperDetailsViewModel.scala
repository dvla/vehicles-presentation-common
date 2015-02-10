package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Form

case class BusinessKeeperDetailsViewModel(form: Form[BusinessKeeperDetailsFormModel],
                                          vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel)
