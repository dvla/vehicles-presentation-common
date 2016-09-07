package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, getProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class OrdnanceSurveyConfig {
  lazy val baseUrl = getProperty[String]("ordnancesurvey.baseUrl")
  lazy val requestTimeout = getOptionalProperty[Int]("ordnancesurvey.requestTimeout")
    .getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
