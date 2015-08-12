package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.{Seconds, Span}
import play.api.libs.json.{JsString, JsValue, Writes, Json}
import uk.gov.dvla.vehicles.presentation.common.WithApplication
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.{DmsWebEndUserDto, DmsWebHeaderDto}

class VehicleAndKeeperLookupWebServiceImplSpec extends UnitSpec with WireMockFixture {

  "callVehicleAndKeeperLookupService" should {

    "send the serialised json request" in new WithApplication {
      val resultFuture = lookupService.invoke(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/vehicleandkeeper/lookup/v1")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId.value)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }

    "timeout when the endpoint is slow to respond" in new WithApplication {
      val requestTimeout: Int = 1
      val requestTimeoutMillis: Int = requestTimeout * 1000
      val futureTimeout = Timeout(Span(requestTimeout * 4, Seconds))

      import com.github.tomakehurst.wiremock.client.WireMock.{post, urlEqualTo, aResponse}
      wireMock.register(post(urlEqualTo("/vehicleandkeeper/lookup/v1")).
        willReturn(aResponse().
        withFixedDelay(requestTimeoutMillis * 2)))

      val resultFuture = lookupService.invoke(request, trackingId)
      whenReady(resultFuture.failed, timeout) { e =>
        e shouldBe a [java.util.concurrent.TimeoutException]
      }
    }
  }

  private val lookupService = new VehicleAndKeeperLookupWebServiceImpl(
    new TestVehicleAndKeeperLookupConfig(wireMockPort))

  private final val trackingId = TrackingId("track-id-test")

  private val request = VehicleAndKeeperLookupRequest(
    dmsHeader = buildHeader,
    referenceNumber = "ref number",
    registrationNumber = "reg number",
    transactionTimestamp = new DateTime
  )

  // Handles this type of formatted string 2014-03-04T00:00:00.000Z
  implicit val jodaISODateWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(dateTime: DateTime): JsValue = {
      val formatter = ISODateTimeFormat.dateTime
      JsString(formatter.print(dateTime))
    }
  }

  private implicit val vehicleAndKeeperDetailsFormat = Json.format[VehicleAndKeeperLookupRequest]

  private def buildHeader: DmsWebHeaderDto = {
    DmsWebHeaderDto(conversationId = "",
      originDateTime = new DateTime,
      applicationCode = "TST",
      channelCode = "TSTWeb",
      contactId = 1,
      eventFlag = true,
      serviceTypeCode = "TST",
      languageCode = "EN",
      endUser = None)
  }

  private def buildEndUser: Option[DmsWebEndUserDto] = {
    Some(DmsWebEndUserDto(endUserTeamCode = "TMC",
      endUserTeamDesc = "Team",
        endUserRole = "Role",
      endUserId = "Id",
      endUserIdDesc = "Id Desc",
      endUserLongNameDesc = "Long name"))
  }
}

class TestVehicleAndKeeperLookupConfig(wireMockPort: Int) extends VehicleAndKeeperLookupConfig {
  override lazy val vehicleAndKeeperLookupMicroServiceBaseUrl = s"http://localhost:$wireMockPort"
  override lazy val requestTimeout: Int = 500
}