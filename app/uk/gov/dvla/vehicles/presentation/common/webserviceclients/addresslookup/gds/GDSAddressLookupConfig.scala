package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class GDSAddressLookupConfig {
  lazy val baseUrl = getProperty[String]("gdsaddresslookup.baseUrl")
  lazy val authorisation = getProperty[String]("gdsaddresslookup.authorisation")
  lazy val requestTimeout = getProperty[Int]("gdsaddresslookup.requestTimeout")
//  val requestTimeout = getProperty("gdsaddresslookup.requestTimeout", 5.seconds.toMillis.toInt)
}
