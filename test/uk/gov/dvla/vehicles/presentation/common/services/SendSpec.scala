package uk.gov.dvla.vehicles.presentation.common.services

import org.joda.time.Instant
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.TrackingId
import common.services.SEND.{Contents, EmailConfiguration, mailtoOps, NoEmailOps}
import common.{UnitSpec, TestWithApplication}
import common.webserviceclients.emailservice.{EmailServiceSendResponse, EmailServiceSendRequest, EmailService, From}
import common.webserviceclients.emailservice.EmailServiceImpl.{ServiceName => EmailServiceName}
import common.webserviceclients.healthstats.{HealthStatsFailure, HealthStatsSuccess, HealthStats}

class SendSpec extends UnitSpec {

  implicit val emailConfiguration = EmailConfiguration(
                                      From("donotreplypronline@dvla.gsi.gov.uk", "DO-NOT-REPLY"),
                                      From("some@feedback", "dummy Feedback email"),
                                      Some(List("@valtech.co.uk","@dvla.gsi.gov.uk","@digital.dvla.gov.uk"))
                                    )
  "whitelist" should {
    "return true if an email belongs to this list" in new TestWithApplication {
      val receivers = List("test@valtech.co.uk")
      SEND.isWhiteListed(receivers) shouldEqual true
    }

    "return false if an email doesn't belong to this list" in new TestWithApplication {
      val receivers = List("test@gmail.com")
      SEND.isWhiteListed(receivers) shouldEqual false
    }

    "return true for any email if the white list is not configured" in new TestWithApplication() {
      implicit val emailConfiguration = EmailConfiguration(
        From("donotreplypronline@dvla.gsi.gov.uk", "DO-NOT-REPLY"),
        From("some@feedback", "dummy Feedback email"),
        None
      )
      val receivers = List("test@anything.com")
      SEND.isWhiteListed(receivers) shouldEqual true
    }
  }

  "Adding people to the email" should {
    "add people if it is a list" in {
      val template = Contents("<h1>Email</h1>", "text email")
      val receivers = List("test@valtech.co.uk")
      val email = SEND email template withSubject ""
      val appendedEmail = email.to(receivers)

      appendedEmail.toPeople.getOrElse(List()).toArray should equal (receivers.toArray)
    }

    "add people if we add one by one" in {
      val template = Contents("<h1>Email</h1>", "text email")
      val person1 = "test1@valtech.co.uk"
      val person2 = "test2@valtech.co.uk"

      val email = SEND email template withSubject ""
      val appendedEmail = email to (person1, person2)

      appendedEmail.toPeople.getOrElse(List()).toArray should equal (Array(person1, person2))
    }

    "add people ic cc" in {
      val template = Contents("<h1>Email</h1>", "text email")
      val person1 = "test1@valtech.co.uk"
      val person2 = "test2@valtech.co.uk"

      val email = SEND email template withSubject "" cc (person1, person2)

      email.ccPeople.getOrElse(List()).toArray should equal (Array(person1, person2))
    }
  }

  "Adding a template and some addresses" should {
    "create a MicroServiceEmailOps if the user belongs to the whitelist" in new TestWithApplication {
      val template = Contents("<h1>Email</h1>", "text email")
      val receivers = List("test@valtech.co.uk")
      val email = SEND email template withSubject "Some Subject" to receivers

      email shouldBe a[SEND.Email]
      mailtoOps(email) shouldBe a[SEND.MicroServiceEmailOps]
    }

    "create a NonWhiteList if the user doesn't belong to the whitelist" in new TestWithApplication {
      val template = Contents("<h1>Email</h1>", "text email")
      val receivers = List("test@broken.co.uk")
      val email = SEND email template withSubject "Some Subject" to receivers

      email shouldBe a[SEND.Email]
      mailtoOps(email) shouldBe a[SEND.NonWhiteListedEmailOps]
    }

    "create a NoEmailOps if the email doesn't have any senders" in new TestWithApplication {
      val template = Contents("<h1>Email</h1>", "text email")
      val email = SEND email template withSubject "Some Subject"

      mailtoOps(email) shouldBe NoEmailOps
    }

    "send an email if the email is ok" in new TestWithApplication {
      implicit val emailConfiguration = EmailConfiguration(
        From("donotreplypronline@dvla.gsi.gov.uk", "DO-NOT-REPLY"),
        From("some@feedback", "dummy Feedback email"),
        None)

      val template = Contents("<h1>Email</h1>", "text email")
      val receivers = List("makis.arvin@gmail.com")
      val email = SEND email template withSubject "Some Subject" to receivers

      mailtoOps(email) shouldBe a[SEND.MicroServiceEmailOps]
    }
  }

  "When failing to send a message to the email micro service the SEND service" should {
    "inform the health stats service of failure" in new TestWithApplication {
      implicit val emailServiceMock = mock[EmailService]
      when(emailServiceMock.invoke(any[EmailServiceSendRequest](), any[TrackingId]))
        .thenReturn(Future.failed(new RuntimeException("BOOM")))
      implicit val dateServiceMock = mock[DateService]
      val expectedInstant = new Instant(0)
      when(dateServiceMock.now).thenReturn(expectedInstant)
      implicit val healthStatsMock = mock[HealthStats]

      val template = Contents("<h1>Email</h1>", "text email")
      val receivers = List("test@valtech.co.uk")
      SEND email template withSubject "Some Subject" to receivers send TrackingId("test-tracking-id")

      verify(healthStatsMock, org.mockito.Mockito.timeout(5000).times(1)).failure(any[HealthStatsFailure])
    }
  }

  "When successfully sending a message to the email micro service the SEND service" should {
    "inform the health stats service of success" in new TestWithApplication {
      implicit val emailServiceMock = mock[EmailService]
      when(emailServiceMock.invoke(any[EmailServiceSendRequest](), any[TrackingId]))
        .thenReturn(Future(EmailServiceSendResponse()))
      implicit val dateServiceMock = mock[DateService]
      val expectedInstant = new Instant(0)
      when(dateServiceMock.now).thenReturn(expectedInstant)
      implicit val healthStatsMock = mock[HealthStats]

      val template = Contents("<h1>Email</h1>", "text email")
      val receivers = List("test@valtech.co.uk")
      SEND email template withSubject "Some Subject" to receivers send TrackingId("test-tracking-id")

      verify(healthStatsMock, org.mockito.Mockito.timeout(5000).times(1)).success(HealthStatsSuccess(EmailServiceName, expectedInstant))
    }
  }
}
