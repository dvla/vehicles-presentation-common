package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, getProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class EmailServiceConfig {
  lazy val emailServiceMicroServiceBaseUrl: String =
    getProperty[String]("emailServiceMicroServiceUrlBase")
  lazy val requestTimeout = getOptionalProperty[Int]("emailService.ms.requesttimeout")
    .getOrElse(CommonConfig.DEFAULT_REQ_TIMEOUT.seconds.toMillis.toInt)
}
