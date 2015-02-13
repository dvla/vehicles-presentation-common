package uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats

import com.google.inject.Inject
import org.joda.time.Instant
import uk.gov.dvla.vehicles.presentation.common.services.DateService

import scala.collection.JavaConversions.asScalaBuffer
import Math.max

import scala.collection.mutable

sealed trait HealthStatsEvent {
  val msName: String
  val time: Instant
}

case class HealthStatsSuccess(msName: String, time: Instant) extends HealthStatsEvent

case class HealthStatsFailure(msName: String, time: Instant, t: Throwable) extends HealthStatsEvent

class HealthStats @Inject()(config: HealthStatsConfig, dateService: DateService) {
  private type MsStats = mutable.ArrayBuffer[HealthStatsEvent]
  private type Stats = collection.mutable.HashMap[String, MsStats]
  private type FailCounts = collection.mutable.HashMap[String, Int]

  private val events = new Stats()
  private val consecutiveFailCounts = new FailCounts()

  def failure(failure: HealthStatsFailure): Unit = this.synchronized {
    if (config.numberOfConsecutiveFailures > 0)
      consecutiveFailCounts.put(failure.msName, consecutiveFailCounts.getOrElse(failure.msName, 0) + 1)
    events.put(
      failure.msName,
      events.getOrElse(failure.msName, new MsStats()).:+(failure)
    )
  }

  def success(success: HealthStatsSuccess): Unit = this.synchronized {
    consecutiveFailCounts.put(success.msName, 0)
    events.put(
      success.msName,
      events.getOrElse(success.msName, new MsStats()).:+(success)
    )
  }

  def healthy: Boolean = this.synchronized {
    try !hasConsecutive(events) && events.exists { case (_, eventsPerMs) =>
      if (config.numberOfRequests < 0) true
      else if (config.numberOfRequests < requestRate(eventsPerMs, dateService.now))
        !hasRelativeFailures(eventsPerMs, oldThreshold)
      else !hasAbsoluteFailures(eventsPerMs, oldThreshold)
    } finally events.transform((_, value) => value.dropWhile(_.time.isBefore(oldThreshold)))
  }


  private def oldThreshold = dateService.now.minus(max(
    max(config.failuresRatioPercentTimeFrame, config.numberOfFailuresTimeFrame),
    max(0, config.numberOfRequestsTimeFrame)
  ))

  private def hasConsecutive(events: Stats): Boolean = consecutiveFailCounts.values.exists(_ > 0)

  private def requestRate(events: MsStats, now: Instant): Float =
    if (events.isEmpty || config.numberOfRequestsTimeFrame < 1) 0
    else events.size * config.numberOfRequestsTimeFrame / now.minus(events.head.time.getMillis).getMillis

  private def hasAbsoluteFailures(events: MsStats, oldThreshold: Instant): Boolean =
    if (config.numberOfFailures < 0) false
    else events.foldRight(config.numberOfFailures)((event, numberOfFailures) =>
      if(event.time.isBefore(oldThreshold)) return false
      else if(numberOfFailures < 1) return true
      else if (event.isInstanceOf[HealthStatsFailure]) numberOfFailures - 1
      else numberOfFailures
    ) < 1


  private def hasRelativeFailures(events: MsStats, oldThreshold: Instant): Boolean =
    if (events.isEmpty || config.failuresRatioPercent < 0) false
    else events
      .dropWhile(_.time.isBefore(oldThreshold))
      .foldLeft((0,0)) {case ((successCount, failureCount), event) =>
        if (event.isInstanceOf[HealthStatsFailure]) (successCount, failureCount + 1)
          else (successCount + 1, failureCount)
    } match {
      case (successCount, failureCount) =>
        failureCount * 100 / max(1, (successCount + failureCount)) >= config.failuresRatioPercent
    }
}
