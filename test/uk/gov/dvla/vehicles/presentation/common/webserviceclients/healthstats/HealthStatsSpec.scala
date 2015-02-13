package uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats

import org.joda.time.Instant
import org.mockito.Mockito.when
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import scala.concurrent.duration.{Duration, DAYS}

class HealthStatsSpec extends UnitSpec {
  val t = new Exception()
  "Not count at all if all the time intervals are -1" in {
    implicit val (dateService, _, service) = setup

    for (i <- 0 to 5000) {
      failure("testms", i)
      success("testms", i)
    }

    when(dateService.now).thenReturn(new Instant(Long.MaxValue))
    service.healthy should be(None)
  }

  "Check healthy consecutive" should {
    "report not healthy if there are x consecutive failures even if there are no relative or absolute count failures" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfConsecutiveFailures).thenReturn(4)

      success("testms", 0)
      success("testms", 10)
      success("testms", 100)

      failure("testms", 111)
      failure("testms", 1110)
      failure("testms", 11100000)
      failure("testms", 11100000000L)
      failure("testms", Long.MaxValue - 1000)

      when(dateService.now).thenReturn(new Instant(Long.MaxValue))
      service.healthy should be(Some(NotHealthyStats("testms", "More then 4 consecutive failures in testms")))
    }

    "report not healthy if there are x consecutive failures for one ms and success only for other" in {
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
      service.healthy should be(Some(NotHealthyStats("testms1", "More then 4 consecutive failures in testms1")))
    }

    "report healthy if there are x consecutive failures followed by a single success" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfConsecutiveFailures).thenReturn(4)

      failure("testms1", 111)
      failure("testms1", 1110)
      failure("testms1", 11100000)
      failure("testms1", 11100000000L)
      failure("testms1", Long.MaxValue - 1000)
      success("testms1", Long.MaxValue - 999)

      when(dateService.now).thenReturn(new Instant(Long.MaxValue))
      service.healthy should be(None)
    }
  }

  "Check healthy failure ratio when request rate is above x" should {
    "report healthy if the failure rate is 100%" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1)
      when(config.numberOfRequestsTimeFrame).thenReturn(Long.MaxValue)
      when(config.failuresRatioPercent).thenReturn(100)
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      failure("test1", 10000)

      when(dateService.now).thenReturn(new Instant(10001))
      service.healthy should be(Some(NotHealthyStats(
        "test1",
        "test1 has 100% for the last 100000ms This is more then configured threshold of 100% failures"
      )))

      success("test1", 10004)
      when(dateService.now).thenReturn(new Instant(11001))
      service.healthy should be(None)
    }

    "report not healthy if the the failure rate is 0%" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1)
      when(config.numberOfRequestsTimeFrame).thenReturn(Duration(10000, DAYS).toMillis)
      when(config.failuresRatioPercent).thenReturn(0)
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      success("test1", 10000)

      when(dateService.now).thenReturn(new Instant(10001))
      service.healthy should be(Some(NotHealthyStats(
        "test1",
        "test1 has 0% for the last 100000ms This is more then configured threshold of 0% failures"
      )))
    }

    "report not healthy if the the failure rate is 50%" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1)
      when(config.numberOfRequestsTimeFrame).thenReturn(Duration(10000, DAYS).toMillis)
      when(config.failuresRatioPercent).thenReturn(50)
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      success("test1", 10000)
      failure("test1", 10001)

      when(dateService.now).thenReturn(new Instant(10004))
      service.healthy should be(Some(NotHealthyStats(
        "test1",
        "test1 has 50% for the last 100000ms This is more then configured threshold of 50% failures"
      )))

      success("test1", 10005)
      when(dateService.now).thenReturn(new Instant(10006))
      service.healthy should be(None)
    }

    "report not healthy if the the failure rate is 26%" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1)
      when(config.numberOfRequestsTimeFrame).thenReturn(Duration(10000, DAYS).toMillis)
      when(config.failuresRatioPercent).thenReturn(26)
      when(config.failuresRatioPercentTimeFrame).thenReturn(100000)

      for (i <- 0 until 3600) failure("ms1", 10000 + i)
      for (i <- 0 until 7400) success("ms1", 20000 + i)

      when(dateService.now).thenReturn(new Instant(100000))
      service.healthy should be(Some(NotHealthyStats(
        "ms1",
        "ms1 has 32% for the last 100000ms This is more then configured threshold of 26% failures"
      )))

      for (i <- 0 until 3000) success("ms1", 20000)

      when(dateService.now).thenReturn(new Instant(200000))
      service.healthy should be(None)
    }

    "eviction of the events of of 1000ms time frame" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(1)
      when(config.numberOfRequestsTimeFrame).thenReturn(1000000)
      when(config.failuresRatioPercent).thenReturn(26)
      when(config.failuresRatioPercentTimeFrame).thenReturn(1000)

      for (i <- 0 until 1000) success("ms1", 8999 + i)

      for (i <- 0 until 26) failure("ms1", 10000 + i)
      for (i <- 0 until 74) success("ms1", 10026 + i)

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(Some(NotHealthyStats(
        "ms1", "ms1 has 26% for the last 1000ms This is more then configured threshold of 26% failures"
      )))

      success("ms1", 10101)

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(None)
    }
  }

  "Check healthy failure count when request rate is below x" should {
    "report healthy for request failures below the threshold" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(100000)
      when(config.numberOfRequestsTimeFrame).thenReturn(1)
      when(config.numberOfFailures).thenReturn(5)
      when(config.numberOfFailuresTimeFrame).thenReturn(1000)

      for (i <- 0 until 6) failure("ms1", 10000 + i)
      for (i <- 0 until 74) success("ms1", 10026 + i)

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(Some(NotHealthyStats("ms1", "ms1 has more then 5 failures for the last 5ms")))
    }

    "report not healthy for request failures above or equal the threshold" in {
      implicit val (dateService, config, service) = setup
      when(config.numberOfRequests).thenReturn(100000)
      when(config.numberOfRequestsTimeFrame).thenReturn(1)
      when(config.numberOfFailures).thenReturn(5)
      when(config.numberOfFailuresTimeFrame).thenReturn(1000)

      for (i <- 0 until 999) failure("ms1", 8999 + i)

      for (i <- 0 until 4) failure("ms1", 10000 + i)
      for (i <- 0 until 74) success("ms1", 10026 + i)

      when(dateService.now).thenReturn(new Instant(11000))
      service.healthy should be(None)
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
    service.failure(HealthStatsFailure(msName, instant, t))
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
