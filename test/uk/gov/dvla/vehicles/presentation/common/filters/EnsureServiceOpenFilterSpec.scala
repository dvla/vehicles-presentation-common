package uk.gov.dvla.vehicles.presentation.common.filters

import java.util.Locale
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Mockito.when
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.contentAsString
import play.api.test.Helpers.defaultAwaitTimeout
import play.twirl.api.{Html, HtmlFormat}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.existentials
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, WithApplication}

class EnsureServiceOpenFilterSpec extends UnitSpec {
  private val hourMilllis = 60 * 60 * 1000

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

  "The out of hours message contains the hours from the config" in new WithApplication {
    val requestHeader = mock[RequestHeader]
    when(requestHeader.path).thenReturn("some-test-path")
    val next = (request:RequestHeader) => Future.successful[Result](fail("Should not come here"))
    val dateTime = new DateTime()
    setUpOutOfHours((setup: SetUp) => {
      val result = setup.filter.apply(next)(requestHeader)
      val resultString = contentAsString(result)
      resultString should include(h(setup.opening))
      resultString should include(h(setup.closing))
    }, dateTime)

    setUpOutOfHours((setup: SetUp) => {
      val result = setup.filter.apply(next)(requestHeader)
      val resultString = contentAsString(result)
      resultString should include(h(setup.opening))
      resultString should include(h(setup.closing))
    }, dateTime, new DateTimeZoneService {
      override def currentDateTimeZone = DateTimeZone.forID("Europe/London")
    })

    def h(hour: Long) =
      DateTimeFormat.forPattern("HH:mm").withLocale(Locale.UK)
        .print(new DateTime(hour * 3600000, DateTimeZone.forID("UTC"))).toLowerCase
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
                           nextFilter: MockFilter,
                           opening: Long,
                           closing: Long)

  private def setUpInHours(test: SetUp => Any, dateTime: DateTime): Unit = {
    val opening = Math.min(0, dateTime.getMillisOfDay / hourMilllis - 1)
    val closing = Math.max(23, dateTime.getMillisOfDay / hourMilllis + 1)
    setUp(test, opening, closing)
  }

  private def setUpInHours(test: SetUp => Any, dateTimeZoneService: DateTimeZoneService): Unit = {
    setUp(test, 8, 18, dateTimeZoneService)
  }

  private def setUpOutOfHours(test: SetUp => Any, dateTime: DateTime): Unit =
    if (dateTime.getMillisOfDay / hourMilllis >= 12) setUp(test, 0, 1)
    else setUp(test, 13, 14)

  private def setUpOutOfHours(test: SetUp => Any,
                              dateTime: DateTime,
                              dateTimeZoneService: DateTimeZoneService): Unit = {
    val opening = dateTime.getMillisOfDay - 2
    val closing = dateTime.getMillisOfDay - 1
    setUp(test, opening, closing, dateTimeZoneService)
  }

  private def setUp(test: SetUp => Any,
                    opening: Int = 0,
                    closing: Int = 24,
                    dateTimeZoneService: DateTimeZoneService = new DateTimeZoneServiceImpl) {
    val sessionFactory =  org.scalatest.mock.MockitoSugar.mock[ClientSideSessionFactory]

    case class TestServiceOpenFilter(override val opening: Int,
                                override val closing: Int,
                                override val dateTimeZone: DateTimeZoneService) extends EnsureServiceOpenFilter {
      override val html = Html("")

      override def html(o: String, c: String): HtmlFormat.Appendable =
        Html(s"opening hours $o to $c")
    }

    test(SetUp(
      filter = TestServiceOpenFilter(opening, closing, dateTimeZoneService),
      request = FakeRequest(),
      sessionFactory = sessionFactory,
      nextFilter = new MockFilter(),
      opening,
      closing
    ))
  }

}
