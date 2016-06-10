package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.ordnance_survey

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.OrdnanceSurveyConfig

import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeOrdnanceSurveyConfig extends OrdnanceSurveyConfig {
  override lazy val baseUrl = getOptionalProperty[String]("ordnancesurvey.baseUrl").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  override lazy val requestTimeout = getOptionalProperty[Int]("ordnancesurvey.requestTimeout").getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
