package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp, intProp}
import scala.concurrent.duration.DurationInt

class OrdnanceSurveyConfig {
  lazy val baseUrl = getOptionalProperty[String]("ordnancesurvey.baseUrl").getOrElse("")
  lazy val requestTimeout = getOptionalProperty[Int]("ordnancesurvey.requestTimeout").getOrElse(5.seconds.toMillis.toInt)
}
