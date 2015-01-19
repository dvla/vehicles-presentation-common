package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.gds

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.GDSAddressLookupConfig
import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeGDSAddressLookupConfig extends GDSAddressLookupConfig {
  override lazy val baseUrl = getOptionalProperty[String]("gdsaddresslookup.baseUrl").getOrElse("")
  override lazy val authorisation = getOptionalProperty[String]("gdsaddresslookup.authorisation").getOrElse("")
  override lazy val requestTimeout = getOptionalProperty[Int]("gdsaddresslookup.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
}
