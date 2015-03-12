package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.{optional, text}
import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views.constraints.FleetNumber.fleetNumber

object FleetNumber {
  def fleetNumberMapping: Mapping[String] = text verifying fleetNumber
}
