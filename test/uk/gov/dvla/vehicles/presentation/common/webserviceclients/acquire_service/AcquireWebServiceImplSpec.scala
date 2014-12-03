package uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json.{JsString, JsValue, Writes, Json}
import uk.gov.dvla.vehicles.presentation.common.{WithApplication, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.{VssWebHeaderDto, VssWebEndUserDto}
import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}

class AcquireWebServiceImplSpec extends UnitSpec with WireMockFixture {

  implicit val noCookieFlags = new NoCookieFlags
  implicit val clientSideSessionFactory = new ClearTextClientSideSessionFactory()
  val acquireService = new AcquireWebServiceImpl(new AcquireConfig() {
    override val baseUrl = s"http://localhost:$wireMockPort"
  })

  private final val trackingId = "track-id-test"

  // Handles this type of formatted string 2014-03-04T00:00:00.000Z
  implicit val jodaISODateWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(dateTime: DateTime): JsValue = {
      val formatter = ISODateTimeFormat.dateTime
      JsString(formatter.print(dateTime))
    }
  }

  implicit val titleTypeFormat = Json.format[TitleTypeDto]
  implicit val keeperDetailsFormat = Json.format[KeeperDetailsDto]
  implicit val traderDetailsFormat = Json.format[TraderDetailsDto]
  implicit val acquireRequestFormat = Json.format[AcquireRequestDto]

  val titleType = TitleTypeDto(Some(1), None)
  val keeperDetails = KeeperDetailsDto(keeperTitle = titleType,
    KeeperBusinessName = None,
    keeperForename = Some("forename"),
    keeperSurname = Some("surname"),
    keeperDateOfBirth = None,
    keeperAddressLines = Seq("a", "b"),
    keeperPostTown = "post town",
    keeperPostCode = "QQ99QQ",
    keeperEmailAddress = None,
    keeperDriverNumber = None)

  val traderDetails = Some(TraderDetailsDto(
    traderOrganisationName = "Org name",
    traderAddressLines = Seq("a", "b"),
    traderPostTown = "post town",
    traderPostCode = "QQ99QQ",
    traderEmailAddress = None))

  val request = AcquireRequestDto(webHeader = new VssWebHeaderDto("1", new DateTime, "A", "B", VssWebEndUserDto("ORG","ENDUSERID")),
    referenceNumber = "ref num",
    registrationNumber = "vrm",
    keeperDetails,
    traderDetails,
    fleetNumber = None,
    dateOfTransfer = new DateTime().toString,
    mileage = None,
    keeperConsent = true,
    transactionTimestamp = new DateTime().toString,
    requiresSorn = false)

  "callAcquireService" should {
    "send the serialised json request" in new WithApplication {
      val resultFuture = acquireService.callAcquireService(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/vehicles/acquire/v1")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }
}
