package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.nonEmptyText
import play.api.data.Mapping

object DropDown {
  def addressDropDown: Mapping[String] = nonEmptyText
}
