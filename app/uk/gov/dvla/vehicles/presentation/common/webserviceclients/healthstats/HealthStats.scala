package uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats

import java.io.{PrintWriter, Writer}

import com.google.inject.Inject
import org.joda.time.Instant
import uk.gov.dvla.vehicles.presentation.common.services.DateService

import Math.max
import play.api.Logger

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait HealthStatsEvent {
  val msName: String
  val time: Instant
}

case class HealthStatsSuccess(msName: String, time: Instant) extends HealthStatsEvent

case class HealthStatsFailure(msName: String, time: Instant, t: Throwable) extends HealthStatsEvent

case class NotHealthyStats(msName: String, details: String)

class HealthStats @Inject()(config: HealthStatsConfig, dateService: DateService) {
  private type MsStats = mutable.ArrayBuffer[HealthStatsEvent]
  private type Stats = collection.mutable.HashMap[String, MsStats]
  private type FailCounts = collection.mutable.HashMap[String, Int]

  private val events = new Stats()
  private val consecutiveFailCounts = new FailCounts()

  def report[T](msName: String)
               (future: Future[T]): Future[T] = {
    future.onSuccess {
      case _ => success(HealthStatsSuccess(msName, dateService.now))
    }
    future.onFailure {
      case e: Throwable =>
        failure(HealthStatsFailure(msName, dateService.now, e))
    }
    future
  }

  def failure(failure: HealthStatsFailure): Unit = this.synchronized {
    if (config.numberOfConsecutiveFailures > 0)
      consecutiveFailCounts.put(failure.msName, consecutiveFailCounts.getOrElse(failure.msName, 0) + 1)
    if (!events.contains(failure.msName)) events.put(failure.msName, new MsStats())
    events.get(failure.msName).get.append(failure)
  }

  def success(success: HealthStatsSuccess): Unit = this.synchronized {
    if (config.numberOfConsecutiveFailures > 0)
      consecutiveFailCounts.put(success.msName, 0)
    if (!events.contains(success.msName)) events.put(success.msName, new MsStats())
    events.get(success.msName).get.append(success)
  }

  def healthy: Option[NotHealthyStats] = this.synchronized {
    val healthyStatus = try hasConsecutive(events) orElse {
      events.foreach { case (msName, eventsPerMs) =>
        if (config.numberOfRequests < 0) return None
        else if (config.numberOfRequests < requestRate(eventsPerMs, dateService.now, numberOfRequestsThreshold))
          hasRelativeFailures(msName, eventsPerMs, relativeFailuresThreshold) match {
            case Some(result) => return Some(result)
            case _ =>
          }
        else hasAbsoluteFailures(eventsPerMs, numberOfFailuresThreshold) match {
          case Some(result) => return Some(result)
          case _ =>
        }
      }
      None
    } finally events.transform((_, value) => value.dropWhile(_.time.isBefore(oldThreshold)))
    // temp comment out as logs are filling up too quickly
//    Logger.debug(s"HealthStats received a healthy query. The answer is $healthyStatus")
    healthyStatus
  }

  def debug(out: PrintWriter): Unit = {
    out.println("--------------- Request rate per MS -----------------")
    out.println(s"Request rate threshold: ${config.numberOfRequests} per ${config.numberOfRequestsTimeFrame}ms")
    events.foreach { case (msName, eventsPerMs) =>
      val requestRateN = requestRate(eventsPerMs, dateService.now, numberOfRequestsThreshold)
      out.println(s"Current requests rate: $msName - $requestRateN requests per ${config.numberOfRequestsTimeFrame}ms")
    }

    out.println("--------------- Consecutive Fail Counts -----------------")
    consecutiveFailCounts.foreach{case (msName, count) => out.println(s"$msName: $count")}
    out.println()

    out.println("======================= Events ==========================")
    out.println()
    events.foreach { case (msName, msStats) =>
      out.println(s"----------------------- $msName -------------------------")
      for (stat <- msStats) out.println(stat)
//      for (i <- 0 until msStats.length) out.println(msStats(i))
    }

  }

  private def hasConsecutive(events: Stats): Option[NotHealthyStats]  = {
    // temp comment out as logs are filling up too quickly
    //Logger.debug(s"HealthStats consecuteFailCounts: $consecutiveFailCounts allEvents: $events")
    consecutiveFailCounts.foreach { case (msName, msFailures) =>
      if (msFailures >= config.numberOfConsecutiveFailures)
        return Some(NotHealthyStats(
          msName, s"The number of consecutive failures in $msName is $msFailures and " +
            s"the fail threshold is ${config.numberOfConsecutiveFailures}"
        ))
    }
    None
  }

  private def requestRate(events: MsStats, now: Instant, numberOfRequestsThreshold: Instant): Float =
    if (events.isEmpty || config.numberOfRequestsTimeFrame < 1) 0
    else events.dropWhile(_.time.isBefore(numberOfRequestsThreshold)).size

  private def hasAbsoluteFailures(events: MsStats, numberOfFailuresThreshold: Instant): Option[NotHealthyStats]  =
    if (config.numberOfFailures < 0) None
    else {
      events.foldRight(config.numberOfFailures)((event, numberOfFailures) =>
        if (event.time.isBefore(numberOfFailuresThreshold)) return None
        else if (event.isInstanceOf[HealthStatsFailure]) {
          if (numberOfFailures < 2)
            return Some(NotHealthyStats(s"${event.msName}", s"${event.msName} has more" +
              s" then ${config.numberOfFailures} failures for the last ${config.numberOfFailures}ms" ))
          numberOfFailures - 1
        }
        else numberOfFailures
      )
      None
    }


  private def hasRelativeFailures(msName: String,
                                  events: MsStats,
                                  relativeFailuresThreshold: Instant): Option[NotHealthyStats]  =
    if (events.isEmpty || config.failuresRatioPercent < 0) None
    else events
      .dropWhile(_.time.isBefore(relativeFailuresThreshold))
      .foldLeft((0,0)) { case ((successCount, failureCount), event) =>
        if (event.isInstanceOf[HealthStatsFailure]) (successCount, failureCount + 1)
        else (successCount + 1, failureCount)
      } match {
        case (successCount, failureCount) =>
          val percentFailures = failureCount * 100 / max(1, successCount + failureCount)
            if( percentFailures >= config.failuresRatioPercent)
              Some(NotHealthyStats(s"$msName", s"$msName has $percentFailures% failures " +
                s"for the last ${config.failuresRatioPercentTimeFrame}ms " +
                s"This is more then configured threshold of ${config.failuresRatioPercent}% failures" ))
            else None
        case _ => None
      }

  private def oldThreshold = dateService.now.minus(max(
    max(config.failuresRatioPercentTimeFrame, config.numberOfFailuresTimeFrame),
    max(0, config.numberOfRequestsTimeFrame)
  ))

  private def relativeFailuresThreshold = dateService.now.minus(max(0, config.failuresRatioPercentTimeFrame))

  private def numberOfFailuresThreshold = dateService.now.minus(max(0, config.numberOfFailuresTimeFrame))

  private def numberOfRequestsThreshold = dateService.now.minus(max(0, config.numberOfRequestsTimeFrame))
}
