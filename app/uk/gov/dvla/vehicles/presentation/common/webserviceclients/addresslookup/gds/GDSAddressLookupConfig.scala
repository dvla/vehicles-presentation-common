package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

import scala.concurrent.duration.DurationInt

class GDSAddressLookupConfig {
  lazy val baseUrl = getOptionalProperty[String]("gdsaddresslookup.baseUrl").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  lazy val authorisation = getOptionalProperty[String]("gdsaddresslookup.authorisation").getOrElse(CommonConfig.DEFAULT_GDS_AUTH)
  lazy val requestTimeout = getOptionalProperty[Int]("gdsaddresslookup.requestTimeout").getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
