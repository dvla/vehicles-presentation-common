package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.ordnance_survey

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.OrdnanceSurveyConfig
import scala.concurrent.duration.DurationInt

/**
 * Fake Configuration that should be used in all the tests
 */
class FakeOrdnanceSurveyConfig extends OrdnanceSurveyConfig {
  override lazy val baseUrl = getOptionalProperty[String]("ordnancesurvey.baseUrl").getOrElse("")
  override lazy val requestTimeout = getOptionalProperty[Int]("ordnancesurvey.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
}
