package uk.gov.dvla.vehicles.presentation.common.k2kandacquire.models

import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel

case class BusinessKeeperDetailsViewModel(form: Form[BusinessKeeperDetailsFormModel],
                                          vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel)
