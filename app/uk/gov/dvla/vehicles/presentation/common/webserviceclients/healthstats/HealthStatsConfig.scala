package uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, intProp, getOptionalDurationProperty}

//trait HealthStatsConfig {
//  val failuresRatioPercent: Int
//  val failuresRatioPercentTimeFrame: Long
//
//  val numberOfFailures: Int
//  val numberOfFailuresTimeFrame: Long
//
//  val numberOfRequests: Int
//  val numberOfRequestsTimeFrame: Long
//
//  val numberOfConsecutiveFailures: Int
//}

class HealthStatsConfig {
  lazy val failuresRatioPercent = getOptionalProperty[Int]("healthStats.failuresRatioPercent").getOrElse(-1)
  lazy val failuresRatioPercentTimeFrame =
    getOptionalDurationProperty("healthStats.failuresRatioPercentTimeFrame").getOrElse(-1L)

  lazy val numberOfFailures = getOptionalProperty[Int]("healthStats.numberOfFailures").getOrElse(-1)
  lazy val numberOfFailuresTimeFrame =
    getOptionalDurationProperty("healthStats.numberOfFailuresTimeFrame").getOrElse(-1L)
  
  lazy val numberOfRequests = getOptionalProperty[Int]("healthStats.numberOfRequests").getOrElse(-1)
  lazy val numberOfRequestsTimeFrame =
    getOptionalDurationProperty("healthStats.numberOfRequestsTimeFrame").getOrElse(-1L)
  
  lazy val numberOfConsecutiveFailures =
    getOptionalProperty[Int]("healthStats.numberOfConsecutiveFailures").getOrElse(-1)
}
