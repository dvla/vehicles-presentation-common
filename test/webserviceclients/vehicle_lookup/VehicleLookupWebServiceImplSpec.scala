package webserviceclients.vehicle_lookup

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlEqualTo}
import helpers.{UnitSpec, WireMockFixture}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.VehicleLookupConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup.{VehicleLookupWebServiceImpl, VehicleDetailsRequestDto}

class VehicleLookupWebServiceImplSpec  extends UnitSpec  with WireMockFixture {

  "callDisposeService" should {
    "send the serialised json request" in {
      val resultFuture = lookupService.callVehicleLookupService(request, trackingId)
      whenReady(resultFuture, timeout) { result =>
        wireMock.verifyThat(1, postRequestedFor(
          urlEqualTo(s"/vehicles/lookup/v1/dispose")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingId)).
          withRequestBody(equalTo(Json.toJson(request).toString())))
      }
    }
  }

  implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
  val lookupService = new VehicleLookupWebServiceImpl(new VehicleLookupConfig() {
    override val baseUrl = s"http://localhost:$wireMockPort"
  })
  implicit val vehiclesDetailsFormat = Json.format[VehicleDetailsRequestDto]

  val trackingId = "track-id-test"

  val request = VehicleDetailsRequestDto(
    referenceNumber = "ref number",
    registrationNumber = "reg number",
    userName = "user name"
  )
}
