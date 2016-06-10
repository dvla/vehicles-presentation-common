package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.gds

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.GDSAddressLookupConfig

import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeGDSAddressLookupConfig extends GDSAddressLookupConfig {
  override lazy val baseUrl = getOptionalProperty[String]("gdsaddresslookup.baseUrl").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  override lazy val authorisation = getOptionalProperty[String]("gdsaddresslookup.authorisation").getOrElse("")
  override lazy val requestTimeout = getOptionalProperty[Int]("gdsaddresslookup.requestTimeout").getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
