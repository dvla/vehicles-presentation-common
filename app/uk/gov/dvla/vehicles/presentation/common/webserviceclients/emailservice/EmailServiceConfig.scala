package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class EmailServiceConfig {
  lazy val emailServiceMicroServiceBaseUrl: String =
    getOptionalProperty[String]("emailServiceMicroServiceUrlBase").getOrElse(CommonConfig.DEFAULT_BASE_URL)
  lazy val requestTimeout = getOptionalProperty[Int]("emailService.ms.requesttimeout").getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
