package uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, TestWithApplication}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders

class EmailServiceWebServiceImplSpec extends UnitSpec with WireMockFixture {

  "call EmailService" should {
    "send the serialised json request" in new TestWithApplication {
      val resultFuture = emailService.invoke(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/email/send")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId.value)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }

  private val emailService = new EmailServiceWebServiceImpl(new TestEmailServiceConfig(wireMockPort))

  private final val trackingId = TrackingId("track-id-test")

  private val request = EmailServiceSendRequest(
    plainTextMessage = "plainTextMessage",
    htmlMessage = "htmlMessage",
    from = From("from@email.com","from"),
    subject = "subject",
    toReceivers = Option(List("to@email.com")),
    ccReceivers = None
  )

  private implicit val emailServiceSendRequestWrites = Json.writes[EmailServiceSendRequest]

}

class TestEmailServiceConfig(wireMockPort: Int) extends EmailServiceConfig {
  override lazy val emailServiceMicroServiceBaseUrl = s"http://localhost:$wireMockPort"
  override lazy val requestTimeout = 5.seconds.toMillis.toInt
}