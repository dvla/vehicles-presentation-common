package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class OrdnanceSurveyConfig {
  val baseUrl = getProperty("ordnancesurvey.baseUrl", "NOT FOUND")
  val requestTimeout = getProperty("ordnancesurvey.requestTimeout", 5.seconds.toMillis.toInt)
}
