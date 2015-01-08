package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicle_lookup

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.webserviceclients.HttpHeaders
import common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import common.{WithApplication, UnitSpec}
import common.testhelpers.WireMockFixture
import common.webserviceclients.config.VehicleLookupConfig
import common.webserviceclients.vehiclelookup.{VehicleLookupWebServiceImpl, VehicleDetailsRequestDto}

class VehicleLookupWebServiceImplSpec  extends UnitSpec  with WireMockFixture {

  "callDisposeService" should {
    "send the serialised json request" in new WithApplication {
      val resultFuture = lookupService.callVehicleLookupService(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/vehicles/lookup/v1/dispose")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }

  implicit val noCookieFlags = new NoCookieFlags
  implicit val clientSideSessionFactory = new ClearTextClientSideSessionFactory()
  val lookupService = new VehicleLookupWebServiceImpl(new TestVehicleLookupConfig(wireMockPort))
  implicit val vehiclesDetailsFormat = Json.format[VehicleDetailsRequestDto]

  val trackingId = "track-id-test"

  val request = VehicleDetailsRequestDto(
    referenceNumber = "ref number",
    registrationNumber = "reg number",
    userName = "user name"
  )
}

class TestVehicleLookupConfig(wireMockPort:Int) extends VehicleLookupConfig {
  override lazy val baseUrl = s"http://localhost:$wireMockPort"
}