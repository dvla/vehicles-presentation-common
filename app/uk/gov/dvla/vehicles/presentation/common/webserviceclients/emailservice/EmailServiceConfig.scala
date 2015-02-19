package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp, intProp}
import scala.concurrent.duration.DurationInt

class EmailServiceConfig {
  lazy val emailServiceMicroServiceBaseUrl: String =
    getOptionalProperty[String]("emailServiceMicroServiceBaseUrl").getOrElse("")
  lazy val requestTimeout = getOptionalProperty[Int]("emailService.ms.requesttimeout").getOrElse(5.seconds.toMillis.toInt)
}
