package uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp}

object ApplicationContext {
  def apply(): String = getOptionalProperty[String]("application.context").getOrElse("")
}