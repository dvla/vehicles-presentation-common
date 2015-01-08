package uk.gov.dvla.vehicles.presentation.common.webserviceclients.config

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class OrdnanceSurveyConfig {
  lazy val baseUrl = getProperty[String]("ordnancesurvey.baseUrl")
//  val requestTimeout = getProperty("ordnancesurvey.requestTimeout", 5.seconds.toMillis.toInt)
  lazy val requestTimeout = getProperty[Int]("ordnancesurvey.requestTimeout")
}
