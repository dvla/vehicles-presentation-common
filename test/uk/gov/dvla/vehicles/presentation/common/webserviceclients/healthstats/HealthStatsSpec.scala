package uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats

import org.joda.time.Instant
import org.mockito.Mockito.when
import org.scalatest.concurrent.Eventually
import scala.concurrent.duration.{Duration, DAYS}
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class HealthStatsSpec extends UnitSpec {

  "Not count at all if all the time intervals are -1" in {
    implicit val (dateService, _, service) = setup

    for (i <- 0 to 5000) {
      failure("testms", i)
      success("testms", i)
    }

    when(dateService.now).thenReturn(new Instant(Long.MaxValue))
    service.healthy should be(None)
  }

  "Check application health via consecutive failures" should {
    "report not healthy if there are x consecutive failures even if there are no relative or absolute count failures" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfConsecutiveFailures).thenReturn(4)

      success("testms", 0)
      success("testms", 10)
      success("testms", 100)

      failure("testms", 111)
      failure("testms", 1110)
      failure("testms", 11100000)
      failure("testms", Long.MaxValue - 1000)

      when(dateService.now).thenReturn(new Instant(Long.MaxValue))
      service.healthy should be(Some(
        NotHealthyStats(
          "testms",
          "The service is not healthy - the number of consecutive failures in testms is 4 and the fail threshold is 4"
        )
      ))
    }

    "report not healthy if there are x consecutive failures for one ms and success only for the other" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfConsecutiveFailures).thenReturn(4)

      success("testms", 0)
      success("testms", 10)
      success("testms", 100)

      failure("testms1", 111)
      failure("testms1", 1110)
      failure("testms1", 11100000)
      failure("testms1", 11100000000L)
      failure("testms1", Long.MaxValue - 1000)

      when(dateService.now).thenReturn(new Instant(Long.MaxValue))
      service.healthy should be(Some(
        NotHealthyStats(
          "testms1",
          "The service is not healthy - the number of consecutive failures in testms1 is 5 and the fail threshold is 4"
        )
      ))
    }

    "report not healthy if the number of consecutive failures is equal to the configured value" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfConsecutiveFailures).thenReturn(1)

      failure("testms1", 111)

      when(dateService.now).thenReturn(new Instant(Long.MaxValue))
      service.healthy should be(Some(
        NotHealthyStats(
          "testms1",
          "The service is not healthy - the number of consecutive failures in testms1 is 1 and the fail threshold is 1"
        )
      ))
    }

    "report healthy if there are x consecutive failures followed by a single success" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfConsecutiveFailures).thenReturn(2)

      failure("testms1", 111)
      failure("testms1", 1110)
      failure("testms1", 11100000)
      success("testms1", Long.MaxValue - 999)

      when(dateService.now).thenReturn(new Instant(Long.MaxValue))
      service.healthy should be(None)
    }
  }

  "Check application health via failure ratio when request rate is above x" should {
    "report unhealthy if the failure rate is 100% and we match it then move the time frame and report healthy" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1) // We need to exceed this request rate to use the failure ratio
      when(config.numberOfRequestsTimeFrame).thenReturn(Long.MaxValue)
      when(config.failuresRatioPercent).thenReturn(100) // Failure rate is 100%
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      // 100% failures to match the configured threshold
      failure("test1", 10000)
      failure("test1", 10000)

      when(dateService.now).thenReturn(new Instant(10001))
      service.healthy should be(Some(NotHealthyStats(
        "test1",
        "The service is not healthy - test1 has 100% failures for the last 100000ms This equals or exceeds the configured threshold of 100% failures"
      )))

      // Move the time frame and now we are healthy
      when(dateService.now).thenReturn(new Instant(11001))
      success("test1", 10004)
      service.healthy should be(None)
    }

    "always report not healthy if the the failure rate is 0%" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1)
      when(config.numberOfRequestsTimeFrame).thenReturn(Duration(10000, DAYS).toMillis)
      when(config.failuresRatioPercent).thenReturn(0) // Failure rate is 0%
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      // 100% success but will result in a status of unhealthy because a failure rate of 0% matches what is configured
      success("test1", 10000)
      success("test1", 10000)

      when(dateService.now).thenReturn(new Instant(10001))
      service.healthy should be(Some(NotHealthyStats(
        "test1",
        "The service is not healthy - test1 has 0% failures for the last 100000ms This equals or exceeds the configured threshold of 0% failures"
      )))
    }

    "report not healthy if the the failure rate is 50% and we match it" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1) // We need to exceed this request rate to use the failure ratio
      when(config.numberOfRequestsTimeFrame).thenReturn(Duration(10000, DAYS).toMillis)
      when(config.failuresRatioPercent).thenReturn(50) // Failure rate is 50%
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      // 50% failure rate
      success("test1", 10000)
      failure("test1", 10001)

      when(dateService.now).thenReturn(new Instant(10004))
      service.healthy should be(Some(NotHealthyStats(
        "test1",
        "The service is not healthy - test1 has 50% failures for the last 100000ms This equals or exceeds the configured threshold of 50% failures"
      )))

      // Move the window and with 100% success we should now be healthy
      when(dateService.now).thenReturn(new Instant(10006))
      success("test1", 10005)
      service.healthy should be(None)
    }

    "report not healthy if the the failure rate is 26% and we exceed it" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1) // We need to exceed this request rate to use the failure ratio
      when(config.numberOfRequestsTimeFrame).thenReturn(Duration(10000, DAYS).toMillis)
      when(config.failuresRatioPercent).thenReturn(26) // Failure rate is 26%
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      for (i <- 1 to 32) failure("ms1", 10000 + i) // 32% failure (we exceed the failure threshold)
      for (i <- 1 to 68) success("ms1", 20000 + i) // 68% success

      when(dateService.now).thenReturn(new Instant(100000))
      service.healthy should be(Some(NotHealthyStats(
        "ms1",
        "The service is not healthy - ms1 has 32% failures for the last 100000ms This equals or exceeds the configured threshold of 26% failures"
      )))

      for (i <- 1 to 10) success("ms1", 20000)

      // Move the window and we should now be healthy
      when(dateService.now).thenReturn(new Instant(200000))
      service.healthy should be(None)
    }

    "when the failure rate is at the threshold and the application is unhealthy a single success event will change it to healthy" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1) // We need to exceed this request rate to use the failure ratio
      when(config.numberOfRequestsTimeFrame).thenReturn(1000000)
      when(config.failuresRatioPercent).thenReturn(26) // Failure rate is 26%
      // This is the time frame we are testing
      // so the time frame will start at 1970-01-01T00:00:10.000Z (10000) and finish at 1970-01-01T00:00:11.000Z (11000)
      when(config.failuresRatioPercentTimeFrame).thenReturn(1000)

      // These are before (to the left of) the window so will have no effect
      for (i <- 1 to 1000) success("ms1", 8999 + i)

      for (i <- 1 to 26) failure("ms1", 10000 + i) // will create 26 failure events at 10000 - 10026
      for (i <- 1 to 74) success("ms1", 10026 + i) // will create 74 success events at 10027 - 10100

      // 11 secs after 1970 eg. 1970-01-01T00:00:11.000Z
      // so the failure time frame will subtract the failuresRatioPercentTimeFrame from now to give 10000 - 11000
      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(Some(NotHealthyStats(
        "ms1",
        "The service is not healthy - ms1 has 26% failures for the last 1000ms This equals or exceeds the configured threshold of 26% failures"
      )))

      // Create a success event immediately after the last success at 10100 this will now reduce the failure rate to
      // less than the 26% threshold and so the service will now report healthy
      success("ms1", 10101)
      service.healthy should be(None)
    }
  }

  "Checking events" should {
    "report healthy when configured numberOfRequests < 0 even if a failure occurs" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(-1) // numberOfRequests needs to be < 0

      // We need a single event to get to the code we want to exercise
      failure("ms1", 1000)

      service.healthy should be(None)
    }
  }

  "Check application health via absolute failure count when request rate is below x" should {
    "report healthy when configured numberOfFailures < 0 even if a failure occurs" in {
      implicit val (dateService, config, service) = setup

      when(config.numberOfRequests).thenReturn(2)
      when(config.numberOfRequestsTimeFrame).thenReturn(1000)
      // This will cause the absolute failure check to return immediately with healthy
      when(config.numberOfFailures).thenReturn(-1)
      when(config.numberOfFailuresTimeFrame).thenReturn(1000)

      // We need a single event to get to the code we want to exercise
      failure("ms1", 1000)
      service.healthy should be(None)
    }

    "report healthy when the number of failures is less than the failure threshold" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(100) // We need fewer requests than what is defined here
      when(config.numberOfRequestsTimeFrame).thenReturn(1)
      when(config.numberOfFailures).thenReturn(3) // Threshold is 3 failures
      when(config.numberOfFailuresTimeFrame).thenReturn(1000)

      // `until` does not include the upper bound use `to` to include it
      for (i <- 1 to 2) failure("ms1", 10000 + i) // Generate 2 failures

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(None)
    }

    "report healthy when the failures exceed the failure threshold but occur before the failure time frame" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(100)
      when(config.numberOfRequestsTimeFrame).thenReturn(1)
      when(config.numberOfFailures).thenReturn(3)
      when(config.numberOfFailuresTimeFrame).thenReturn(1000)

      for (i <- 1 to 5) failure("ms1", 8999 + i)

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(None)
    }

    "report unhealthy when the number of failures equals the failure threshold" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(100) // We need fewer requests than what is defined here
      when(config.numberOfRequestsTimeFrame).thenReturn(1)
      when(config.numberOfFailures).thenReturn(5) // Threshold is 5 failures
      when(config.numberOfFailuresTimeFrame).thenReturn(1000)

      for (i <- 1 to 5) failure("ms1", 10000 + i) // Generate 5 failures
      for (i <- 1 to 10) success("ms1", 10026 + i)

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(Some(NotHealthyStats(
        "ms1",
        "The service is not healthy - ms1 equals or has more than 5 failures (absolute failure count) for the last 1000ms"
      )))
    }

    "report unhealthy when the number of failures exceeds the failure threshold" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(100) // We need fewer requests than what is defined here
      when(config.numberOfRequestsTimeFrame).thenReturn(1)
      when(config.numberOfFailures).thenReturn(3) // Threshold is 3 failures
      when(config.numberOfFailuresTimeFrame).thenReturn(1000)

      for (i <- 1 to 4) failure("ms1", 10000 + i) // Generate 4 failures
      for (i <- 1 to 5) success("ms1", 10026 + i)

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(Some(NotHealthyStats(
        "ms1",
        "The service is not healthy - ms1 equals or has more than 3 failures (absolute failure count) for the last 1000ms"
      )))
    }
  }

  "report method" should {
    "Report success or failure in case of successful or failing future" in {
      implicit val (dateService, config, service) = setup
      when(dateService.now).thenReturn(new Instant(1))
      when(config.numberOfConsecutiveFailures).thenReturn(1)

      val future = Future.successful("test-future-result")
      val retFuture = service.report("test-service") (future)
      retFuture should be theSameInstanceAs future

      when(dateService.now).thenReturn(new Instant(10))
      Eventually.eventually(service.healthy should be(None))

      val e = new Exception()
      when(dateService.now).thenReturn(new Instant(11))
      service.report("test-service") (Future.failed(e))
      when(dateService.now).thenReturn(new Instant(20))
      Eventually.eventually(
        service.healthy should be(Some(
          NotHealthyStats(
            "test-service",
            "The service is not healthy - the number of consecutive failures in test-service is 1 and the fail threshold is 1")
        ))
      )

      when(dateService.now).thenReturn(new Instant(31))
      service.report("test-service") (future)

      when(dateService.now).thenReturn(new Instant(40))
      Eventually.eventually(service.healthy should be(None))
    }
  }

  private def success(msName: String, time: Long)(implicit dateService: DateService, service: HealthStats): Unit = {
    val instant = new Instant(time)
    when(dateService.now).thenReturn(instant)
    service.success(HealthStatsSuccess(msName, instant))
  }

  private def failure(msName: String, time: Long)(implicit dateService: DateService, service: HealthStats): Unit = {
    val instant = new Instant(time)
    when(dateService.now).thenReturn(instant)
    service.failure(HealthStatsFailure(msName, instant, new Exception))
  }

  private def setup: (DateService, HealthStatsConfig, HealthStats) = {
    val config = mock[HealthStatsConfig]
    when(config.numberOfConsecutiveFailures).thenReturn(-1)
    when(config.numberOfFailures).thenReturn(-1)
    when(config.numberOfFailuresTimeFrame).thenReturn(-1)
    when(config.numberOfRequests).thenReturn(-1)
    when(config.numberOfRequestsTimeFrame).thenReturn(-1)
    when(config.failuresRatioPercent).thenReturn(-1)
    when(config.failuresRatioPercentTimeFrame).thenReturn(-1)

    val dateService = mock[DateService]
    when(dateService.now).thenReturn(new Instant(0))

    (dateService, config, new HealthStats(config, dateService))
  }
}
