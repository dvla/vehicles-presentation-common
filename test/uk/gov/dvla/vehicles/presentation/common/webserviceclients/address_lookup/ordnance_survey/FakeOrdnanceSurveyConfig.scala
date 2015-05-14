package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.ordnance_survey

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp, intProp}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.OrdnanceSurveyConfig
import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeOrdnanceSurveyConfig extends OrdnanceSurveyConfig {
  override lazy val baseUrl = getOptionalProperty[String]("ordnancesurvey.baseUrl").getOrElse("")
  override lazy val requestTimeout = getOptionalProperty[Int]("ordnancesurvey.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
}
