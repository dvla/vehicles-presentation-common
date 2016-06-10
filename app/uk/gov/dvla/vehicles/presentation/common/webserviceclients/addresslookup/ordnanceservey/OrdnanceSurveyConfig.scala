package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

import scala.concurrent.duration.DurationInt

class OrdnanceSurveyConfig {
  lazy val baseUrl = getOptionalProperty[String]("ordnancesurvey.baseUrl").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  lazy val requestTimeout = getOptionalProperty[Int]("ordnancesurvey.requestTimeout").getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
