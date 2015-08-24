package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp, intProp}

class EmailServiceConfig {
  lazy val emailServiceMicroServiceBaseUrl: String =
    getOptionalProperty[String]("emailServiceMicroServiceUrlBase").getOrElse("")
  lazy val requestTimeout = getOptionalProperty[Int]("emailService.ms.requesttimeout").getOrElse(5.seconds.toMillis.toInt)
}
