package uk.gov.dvla.vehicles.presentation.common.filters

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.existentials

class EnsureServiceOpenFilterSpec extends UnitSpec {
  private val milliesInAnHour = 3600000

  private val dateTimeService = new DateTimeZoneServiceImpl

  "Return True for an acceptable time, with the current timezone as GMT" in {
    val dateTime = new DateTime(dateTimeService.currentDateTimeZone)
    setUpInHours((setup: SetUp) => {
      setup.filter.serviceOpen(dateTime) should equal(true)
    }, dateTime)
  }

  "Return False for an out of hours time, with the current timezone as GMT" in {
    val dateTime = new DateTime(dateTimeService.currentDateTimeZone)
    setUpOutOfHours((setup: SetUp) => {
      setup.filter.serviceOpen(dateTime) should equal(false)
    }, dateTime)
  }

  "Return True for a timezone time falling within opening hours, and False for a time in another timezone falling outside opening hours" in {
    setUpInHours ((setup: SetUp) => {
        setup.filter.serviceOpen() should equal(true)
    }, new DateTimeZoneService {
      override def currentDateTimeZone: DateTimeZone = DateTimeZone.forOffsetHours(inHoursOffset)
    })

    setUpInHours ((setup: SetUp) => {
        setup.filter.serviceOpen() should equal(false)
    }, new DateTimeZoneService {
      override def currentDateTimeZone: DateTimeZone = DateTimeZone.forOffsetHours(outOfHoursOffset)
    })
  }

  "Return a null next filter request if trying to access the service out of hours" in {
    val dateTime = new DateTime(dateTimeService.currentDateTimeZone)
    setUpOutOfHours((setup: SetUp) => {
      val filterResult: Future[Result] = setup.filter.apply(setup.nextFilter)(setup.request)
      whenReady(filterResult) { result =>
        setup.nextFilter.passedRequest should be(null)
      }
    }, dateTime)
  }

  "Return a valid next filter request if trying to access the service within acceptable hours" in {
    val dateTime = new DateTime(dateTimeService.currentDateTimeZone)
    setUpInHours((setup: SetUp) => {
      val filterResult: Future[Result] = setup.filter.apply(setup.nextFilter)(setup.request)
      whenReady(filterResult) { result =>
        setup.nextFilter.passedRequest.toString() should equal("GET /")
      }
    }, dateTime)
  }

  private class MockFilter extends ((RequestHeader) => Future[Result]) {
    var passedRequest: RequestHeader = _

    override def apply(rh: RequestHeader): Future[Result] = {
      passedRequest = rh
      Future(Results.Ok)
    }
  }

  private def inHoursOffset: Int = {
    val formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")
    timezoneOffset(formatter.parseDateTime("01/01/2014 11:30:30"))
  }

  private def outOfHoursOffset: Int = {
    val formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")
    timezoneOffset(formatter.parseDateTime("01/01/2014 21:30:30"))
  }

  private def timezoneOffset(targetDateTime: DateTime): Int = {
    val currentDateTime = new DateTime(dateTimeService.currentDateTimeZone)
    - (currentDateTime.hourOfDay.get() - targetDateTime.getHourOfDay)
  }

  private case class SetUp(filter: EnsureServiceOpenFilter,
                           request: FakeRequest[_],
                           sessionFactory:ClientSideSessionFactory,
                           nextFilter: MockFilter)

  private def setUpInHours(test: SetUp => Any, dateTime: DateTime): Unit = {
    val opening = dateTime.getMillisOfDay - 5000
    val closing = dateTime.getMillisOfDay + 5000
    setUp(test, opening, closing)
  }

  private def setUpInHours(test: SetUp => Any, dateTimeZoneService: DateTimeZoneService): Unit = {
    setUp(test, 8 * milliesInAnHour, 18 * milliesInAnHour, dateTimeZoneService)
  }

  private def setUpOutOfHours(test: SetUp => Any, dateTime: DateTime): Unit = {
    val opening = dateTime.getMillisOfDay - 2
    val closing = dateTime.getMillisOfDay - 1
    setUp(test, opening, closing)
  }

  private def setUpOutOfHours(test: SetUp => Any, dateTimeZoneService: DateTimeZoneService): Unit = {
    setUp(test, 1 * milliesInAnHour, 1 * milliesInAnHour, dateTimeZoneService)
  }

  private def setUp(test: SetUp => Any,
                    opening: Int = 0,
                    closing: Int = 24 * milliesInAnHour,
                    dateTimeZoneService: DateTimeZoneService = new DateTimeZoneServiceImpl) {
    val sessionFactory =  org.scalatest.mock.MockitoSugar.mock[ClientSideSessionFactory]

    case class TestServiceOpenFilter(override val opening: Int,
                                override val closing: Int,
                                override val dateTimeZone: DateTimeZoneService) extends EnsureServiceOpenFilter {
      protected val html: HtmlFormat.Appendable = Html("")
    }

    test(SetUp(
      filter = TestServiceOpenFilter(opening, closing, dateTimeZoneService),
      request = FakeRequest(),
      sessionFactory = sessionFactory,
      nextFilter = new MockFilter()
    ))
  }

}