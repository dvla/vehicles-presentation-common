package uk.gov.dvla.vehicles.presentation.common

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

object ApplicationContext {
  def apply(): String = getOptionalProperty[String]("application.context").getOrElse(CommonConfig.DEFAULT_APPLICATION_CONTEXT)
}