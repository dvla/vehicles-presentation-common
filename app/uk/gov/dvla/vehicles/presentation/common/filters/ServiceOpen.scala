package uk.gov.dvla.vehicles.presentation.common.filters

import uk.gov.dvla.vehicles.presentation.common.controllers.Version

object ServiceOpen {
  val whitelist = List (
    "/assets/",
    "/feedback",
    "/healthcheck",
    Version.Suffix
  )
}
