package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class GDSAddressLookupConfig {
  val baseUrl = getProperty("gdsaddresslookup.baseUrl", "")
  val authorisation = getProperty("gdsaddresslookup.authorisation", "")
  val requestTimeout = getProperty("gdsaddresslookup.requestTimeout", 5.seconds.toMillis.toInt)
}
