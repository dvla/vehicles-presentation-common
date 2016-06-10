package uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalDurationProperty
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getOptionalProperty
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.intProp
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

class HealthStatsConfig {
  lazy val failuresRatioPercent = getOptionalProperty[Int]("healthStats.failuresRatioPercent").getOrElse(CommonConfig.DEFAULT_HEALTHSTATS)
  lazy val failuresRatioPercentTimeFrame =
    getOptionalDurationProperty("healthStats.failuresRatioPercentTimeFrame").getOrElse(CommonConfig.DEFAULT_HEALTHSTATS_L)

  lazy val numberOfFailures = getOptionalProperty[Int]("healthStats.numberOfFailures").getOrElse(CommonConfig.DEFAULT_HEALTHSTATS)
  lazy val numberOfFailuresTimeFrame =
    getOptionalDurationProperty("healthStats.numberOfFailuresTimeFrame").getOrElse(CommonConfig.DEFAULT_HEALTHSTATS_L)
  
  lazy val numberOfRequests = getOptionalProperty[Int]("healthStats.numberOfRequests").getOrElse(CommonConfig.DEFAULT_HEALTHSTATS)
  lazy val numberOfRequestsTimeFrame =
    getOptionalDurationProperty("healthStats.numberOfRequestsTimeFrame").getOrElse(CommonConfig.DEFAULT_HEALTHSTATS_L)
  
  lazy val numberOfConsecutiveFailures =
    getOptionalProperty[Int]("healthStats.numberOfConsecutiveFailures").getOrElse(CommonConfig.DEFAULT_HEALTHSTATS)
}
