package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import webserviceclients.emailservice.{EmailServiceSendRequest, EmailServiceWebServiceImpl}
import scala.concurrent.duration.DurationInt

final class EmailServiceWebServiceImplSpec extends UnitSpec with WireMockFixture {

  "call EmailService" should {

    "send the serialised json request" in new WithApplication {
      val resultFuture = emailService.invoke(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/email/send")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }

  private val emailService = new EmailServiceWebServiceImpl(new TestEmailServiceConfig(wireMockPort))

  private final val trackingId = "track-id-test"

  private val request = EmailServiceSendRequest(
    plainTextMessage = "plainTextMessage",
    htmlMessage = "htmlMessage",
    from = From("from@email.com","from"),
    subject = "subject",
    emailAddress = "to@email.com"
  )

  private implicit val emailServiceSendRequestWrites = Json.writes[EmailServiceSendRequest]

}

class TestEmailServiceConfig(wireMockPort: Int) extends EmailServiceConfig {
  override lazy val emailServiceMicroServiceBaseUrl = s"http://localhost:$wireMockPort"
  override lazy val requestTimeout = 5.seconds.toMillis.toInt
}