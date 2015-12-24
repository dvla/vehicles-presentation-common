package uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats

import com.google.inject.Inject
import java.io.PrintWriter
import Math.max
import org.joda.time.Instant
import play.api.Logger
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.dvla.vehicles.presentation.common.services.DateService

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

  private final val unhealthyIntro = "The service is not healthy -"

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

  /**
    * Provides a client with the ability to get debug information out of this service
    * @param out the PrintWriter where the debug will be written
    */
  def debug(out: PrintWriter): Unit = {
    def countAbsoluteFailures() = {
      val numberOfFailuresThreshold = dateService.now.minus(max(0, config.numberOfFailuresTimeFrame))
      events.foreach { case (msName, eventsPerMs) =>
        val count = eventsPerMs.foldRight(0)((event, numberOfFailures) =>
          // As we are processing the most recent events and moving back in time, if we find any event (success or failure)
          // before the start of the time frame then ignore it as we have processed the time frame
          if (event.time.isBefore(numberOfFailuresThreshold)) numberOfFailures
          else if (event.isInstanceOf[HealthStatsFailure]) {
            numberOfFailures + 1 // Increase the count
          }
          else numberOfFailures // Ignore success events
        )
        out.println(s"$msName: $count")
      }
    }
    out.println("=============== Request rate per MS ================")
    out.println(s"Request rate threshold: ${config.numberOfRequests} per ${config.numberOfRequestsTimeFrame}ms")
    events.foreach { case (msName, eventsPerMs) =>
      val requestRateN = requestRate(eventsPerMs, numberOfRequestsThreshold)
      out.println(s"Current requests rate: $msName - $requestRateN requests per ${config.numberOfRequestsTimeFrame}ms")
    }
    out.println()

    out.println("============= Consecutive Fail Counts ==============")
    out.println(s"Consecutive fail threshold: ${config.numberOfConsecutiveFailures}")
    consecutiveFailCounts.foreach{case (msName, count) => out.println(s"$msName: $count")}
    out.println()

    out.println("=============== Absolute Fail Counts ===============")
    out.println(s"Absolute fail threshold: ${config.numberOfFailures}")
    countAbsoluteFailures()
    out.println()

    out.println("====================== Events ======================")
    out.println()
    events.foreach { case (msName, msStats) =>
      out.println(s"----------------------- $msName -------------------------")
      for (stat <- msStats) out.println(stat)
      out.println()
    }
  }

  /**
    * Inform the service that a failure has occurred
    * @param failure Object containing the failure information
    */
  def failure(failure: HealthStatsFailure): Unit = this.synchronized {
    if (config.numberOfConsecutiveFailures > 0)
      consecutiveFailCounts.put(failure.msName, consecutiveFailCounts.getOrElse(failure.msName, 0) + 1)
    if (!events.contains(failure.msName)) events.put(failure.msName, new MsStats())
    events.get(failure.msName).get.append(failure)
  }

  /**
    * Inform the service that a failure has occurred
    * @param msName the micro service name
    * @param t the exception that was thrown when calling the micro service
    */
  def failure(msName: String, t: Throwable): Unit = failure(new HealthStatsFailure(msName, dateService.now, t))

  /**
    * Inform the service that a micro service has been successfully called
    * @param success Case class containing the micro service name and when the successful call was made
    */
  def success(success: HealthStatsSuccess): Unit = this.synchronized {
    if (config.numberOfConsecutiveFailures > 0)
      consecutiveFailCounts.put(success.msName, 0)
    if (!events.contains(success.msName)) events.put(success.msName, new MsStats())
    events.get(success.msName).get.append(success)
  }

  /**
    * Inform the service that a micro service has been successfully called
    * @param msName the micro service name
    */
  def success(msName: String): Unit = success(new HealthStatsSuccess(msName, dateService.now))

  /**
    * Ask the service whether or not the application is healthy
    * @return An Option[A] indicating whether or not the application is healthy.
    *         if the service is not healthy the option will be filled otherwise it will be empty
    */
  def healthy: Option[NotHealthyStats] = this.synchronized {
    val healthyStatus = try hasConsecutiveFailuresThatExceedThreshold orElse checkEvents
    finally dropOldEvents()
    healthyStatus.map(notHealthy => {
      Logger.error(s"${notHealthy.details}.")
      notHealthy
    })
  }

  /**
    * Iterates the consecutiveFailCounts map and if we find a count that matches or exceeds the threshold
    * a NotHealthyStats instance is returned that captures the name of the micro service and some detail
    * text that describes why the micro service is listed
    * @return
    */
  private def hasConsecutiveFailuresThatExceedThreshold: Option[NotHealthyStats]  = {
    // The map contains micro service name and the number of consecutive failures that ms has encountered
    consecutiveFailCounts.foreach { case (msName, msFailureCount) => // iterate the contents of the map
      // If the failure count hits the threshold return the NotHealthyStats case class and the loop terminates early
      if (msFailureCount >= config.numberOfConsecutiveFailures)
        return Some(NotHealthyStats(
          msName, s"$unhealthyIntro the number of consecutive failures in $msName is $msFailureCount and " +
            s"the fail threshold is ${config.numberOfConsecutiveFailures}"
        ))
    }
    None // No micro services have encountered consecutive failures that match the threshold
  }

  /**
    * Process the events to determine if the application is healthy or not
    * @return a filled Option[NotHealthyStats] if the application is unhealthy otherwise None
    */
  private def checkEvents: Option[NotHealthyStats] = {
    def relativeFailuresThreshold = dateService.now.minus(max(0, config.failuresRatioPercentTimeFrame))
    def numberOfFailuresThreshold = dateService.now.minus(max(0, config.numberOfFailuresTimeFrame))
    // Iterate the events for each micro service.
    // This means that the health check mechanism can vary by micro service depending on how many events
    // have been generated in the time window
    events.foreach { case (msName, eventsPerMs) =>
      if (config.numberOfRequests < 0) return None
      // The number of generated events exceeds the configured numberOfRequests for a single micro service
      // so we calculate the health of the micro service by the number of failures in terms of the total number
      // of events (both failures and successes). If the ratio exceeds the configured percentage threshold then
      // the application is unhealthy.
      else if (config.numberOfRequests < requestRate(eventsPerMs, numberOfRequestsThreshold))
        hasRelativeFailures(msName, eventsPerMs, relativeFailuresThreshold) match {
          case Some(result) => return Some(result) // Return early from the loop
          case _ => // Do nothing let the loop continue
        }
      // The number of generated events is less or equal to the configured numberOfRequests for a single micro service
      // so we use the absolute failure count to determine application health
      else hasAbsoluteFailures(eventsPerMs, numberOfFailuresThreshold) match {
        case Some(result) => return Some(result) // Return early from the loop
        case _ => // Do nothing let the loop continue
      }
    }
    None
  }

  /**
    * This method deals with a set of micro service events that belong to a single micro service.
    * This method calculates the number of micro service events that have occurred since the start of the time frame.
    * @param events the events for a single micro service
    * @param numberOfRequestsThreshold the start of the time window we are interested in
    * @return the number of micro service events that have occurred since the start of the time window for a single
    *         micro service
    */
  private def requestRate(events: MsStats, numberOfRequestsThreshold: Instant): Int =
    if (events.isEmpty || config.numberOfRequestsTimeFrame < 1) 0
    else events.dropWhile(_.time.isBefore(numberOfRequestsThreshold)).size

  /**
    * Relative failures is ( failures / failures + successes ) * 100
    * @param msName name of the micro service
    * @param events the collection of events for a micro service
    * @param relativeFailuresThreshold the start of the time frame during which we want to work out if the number
    *                                  of failures compared to the total number of events exceeds or equals the
    *                                  configured failuresRatioPercent
    * @return a filled Option[NotHealthyStats] if the relative failures compared to total number of events exceeds or
    *         equals the failuresRatioPercent. Otherwise return an empty option[NotHealthyStats]
    */
  private def hasRelativeFailures(msName: String,
                                  events: MsStats,
                                  relativeFailuresThreshold: Instant): Option[NotHealthyStats]  =
    if (events.isEmpty || config.failuresRatioPercent < 0) None
    else events
      // Start by removing all events that occurred before the start of the relative failures window
      .dropWhile(_.time.isBefore(relativeFailuresThreshold))
      // Initialise foldLeft with a tuple containing (0, 0). This tuple is the accumulator as we
      // iterate all the events
      .foldLeft((0,0)) { case ((successCount, failureCount), event) => // Note the use of partial function and curly braces
      // Deal with failures by incrementing the failure count
      if (event.isInstanceOf[HealthStatsFailure]) (successCount, failureCount + 1)
      // Otherwise must be success event so increment that count
      else (successCount + 1, failureCount)
    } match {
      // Now process the results of the foldLeft which is a tuple of (number of successes, number of failures)
      case (successCount, failureCount) =>
        // % failures = failures/(successes + failures) * 100
        val percentFailures = failureCount * 100 / max(1, successCount + failureCount)
        // If the % failures exceeds or equals the threshold then return a filled Option[A]
        if (percentFailures >= config.failuresRatioPercent)
          Some(NotHealthyStats(s"$msName", s"$unhealthyIntro $msName has $percentFailures% failures " +
            s"for the last ${config.failuresRatioPercentTimeFrame}ms " +
            s"This equals or exceeds the configured threshold of ${config.failuresRatioPercent}% failures" ))
        else None
      case _ => None
    }

  /**
    * Absolute failures is a count of the number of failures in the given time frame. If it exceeds the configured
    * threshold then the application is considered unhealthy
    * @param events the collection of events for a micro service
    * @param numberOfFailuresThreshold the start of the time frame during which we want to work out if the number
    *                                  of failures exceeds the configured numberOfFailures
    * @return a filled Option[NotHealthyStats] if the number of failures in the given time frame exceeds the
    *         configured amount. Otherwise return an empty option[NotHealthyStats] to indicate a healthy application
    */
  private def hasAbsoluteFailures(events: MsStats, numberOfFailuresThreshold: Instant): Option[NotHealthyStats]  =
    if (config.numberOfFailures < 0) None
    else {
      // numberOfFailures is the accumulator start it with the value that is specified in config, iterate the events
      // and count backwards as we go
      events.foldRight(config.numberOfFailures)((event, numberOfFailures) =>
        // As we are processing the most recent events and moving back in time, if we find any event (success or failure)
        // before the start of the time frame then indicate the app is healthy as we have processed the time frame
        if (event.time.isBefore(numberOfFailuresThreshold)) return None
        else if (event.isInstanceOf[HealthStatsFailure]) {
          if (numberOfFailures < 2)
            return Some(NotHealthyStats(s"${event.msName}", s"$unhealthyIntro ${event.msName} equals " +
              s"or has more than ${config.numberOfFailures} failures (absolute failure count) " +
              s"for the last ${config.numberOfFailuresTimeFrame}ms" ))
          numberOfFailures - 1 // Reduce the accumulator
        }
        else numberOfFailures
      )
      None // The application is healthy
    }

  private def dropOldEvents(): Unit = {
    // Work out the time frame that is the furthest back in time and use that
    def oldThreshold = dateService.now.minus(max(
      max(config.failuresRatioPercentTimeFrame, config.numberOfFailuresTimeFrame),
      max(0, config.numberOfRequestsTimeFrame)
    ))

    // Iterate the event map which is a map of ms name to mutable.ArrayBuffer[HealthStatsEvent]
    // we are only interested in the mutable.ArrayBuffer[HealthStatsEvent], which is the value.
    // We want to drop all HealthStatsEvents that occurred before the old threshold.
    events.transform((_, value) => value.dropWhile(_.time.isBefore(oldThreshold)))
  }

  // This might be set to 10 minutes so the threshold would be 10 minutes ago (now - 10 minutes)
  private def numberOfRequestsThreshold = dateService.now.minus(max(0, config.numberOfRequestsTimeFrame))
}
