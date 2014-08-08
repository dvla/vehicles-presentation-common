package uk.gov.dvla.vehicles.presentation.common.webserviceclients.dispose_service

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.DisposeConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.dispose.{DisposalAddressDto, DisposeRequestDto, DisposeWebServiceImpl}
import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}

class DisposeWebServiceImplSpec extends UnitSpec with WireMockFixture {

  implicit val noCookieFlags = new NoCookieFlags
  implicit val clientSideSessionFactory = new ClearTextClientSideSessionFactory()
  val disposeService = new DisposeWebServiceImpl(new DisposeConfig() {
    override val baseUrl = s"http://localhost:$wireMockPort"
  })

  private final val trackingId = "track-id-test"

  implicit val disposalAddressDtoFormat = Json.format[DisposalAddressDto]
  implicit val disposeRequestFormat = Json.format[DisposeRequestDto]
  val request = DisposeRequestDto(
    referenceNumber = "ref number",
    registrationNumber = "reg number",
    traderName = "trader test",
    traderAddress = DisposalAddressDto(
      line = Seq("line1", "line2"),
      postTown = Some("town"),
      postCode = "W193NE",
      uprn = Some(3123L)
    ),
    dateOfDisposal = "",
    transactionTimestamp = "",
    prConsent = true,
    keeperConsent = false,
    mileage = Some(12)
  )

  "callDisposeService" should {
    "send the serialised json request" in {
      val resultFuture = disposeService.callDisposeService(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/vehicles/dispose/v1")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }
}
