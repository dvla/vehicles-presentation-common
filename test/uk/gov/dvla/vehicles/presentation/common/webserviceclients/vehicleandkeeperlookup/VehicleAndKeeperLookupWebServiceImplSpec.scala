package webserviceclients.vehicleandkeeperlookup

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}
import uk.gov.dvla.vehicles.presentation.common.WithApplication

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.VehicleAndKeeperLookupConfig

final class VehicleAndKeeperLookupWebServiceImplSpec extends UnitSpec with WireMockFixture {

  "callVehicleAndKeeperLookupService" should {

    "send the serialised json request" in new WithApplication {
      val resultFuture = lookupService.invoke(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/vehicleandkeeper/lookup/v1")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }

  private val lookupService = new VehicleAndKeeperLookupWebServiceImpl(new VehicleAndKeeperLookupConfig() {
    override val vehicleAndKeeperLookupMicroServiceBaseUrl = s"http://localhost:$wireMockPort"
  })

  private final val trackingId = "track-id-test"

  private val request = VehicleAndKeeperDetailsRequest(
    referenceNumber = "ref number",
    registrationNumber = "reg number"
  )

  private implicit val vehicleAndKeeperDetailsFormat = Json.format[VehicleAndKeeperDetailsRequest]


  //  "callDisposeService" should {
  //    "send the serialised json request" in new WithApplication {
  //      val resultFuture = lookupService.callVehicleLookupService(request, trackingId)
  //      whenReady(resultFuture, timeout) { result =>
  //        wireMock.verifyThat(1, postRequestedFor(
  //          urlEqualTo(s"/vehicles/lookup/v1/dispose")
  //        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId)).
  //          withRequestBody(equalTo(Json.toJson(request).toString())))
  //      }
  //    }
  //  }
  //
  //  implicit val noCookieFlags = new NoCookieFlags
  //  implicit val clientSideSessionFactory = new ClearTextClientSideSessionFactory()
  //  val lookupService = new VehicleLookupWebServiceImpl(new VehicleLookupConfig() {
  //    override val baseUrl = s"http://localhost:$wireMockPort"
  //  })
  //  implicit val vehiclesDetailsFormat = Json.format[VehicleDetailsRequestDto]
  //
  //  val trackingId = "track-id-test"
  //
  //  val request = VehicleDetailsRequestDto(
  //    referenceNumber = "ref number",
  //    registrationNumber = "reg number",
  //    userName = "user name"
  //  )
  //}
}