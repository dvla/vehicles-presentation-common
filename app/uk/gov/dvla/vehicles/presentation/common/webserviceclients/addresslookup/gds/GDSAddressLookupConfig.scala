package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp, intProp}
import scala.concurrent.duration.DurationInt

class GDSAddressLookupConfig {
  lazy val baseUrl = getOptionalProperty[String]("gdsaddresslookup.baseUrl").getOrElse("")
  lazy val authorisation = getOptionalProperty[String]("gdsaddresslookup.authorisation").getOrElse("")
  lazy val requestTimeout = getOptionalProperty[Int]("gdsaddresslookup.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
}
