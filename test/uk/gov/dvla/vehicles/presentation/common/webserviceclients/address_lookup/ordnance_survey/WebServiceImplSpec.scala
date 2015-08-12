package uk.gov.dvla.vehicles.presentation.common.webserviceclients.address_lookup.ordnance_survey

import com.github.tomakehurst.wiremock.client.WireMock.{getRequestedFor, urlEqualTo, equalTo}
import org.scalatest.concurrent.PatienceConfiguration.Interval
import scala.concurrent.duration.DurationInt
import play.api.i18n.Lang
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{TrackingId, ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.OrdnanceSurveyConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.WebServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeAddressLookupService.{PostcodeValid, PostcodeValidWithSpace}

final class WebServiceImplSpec extends UnitSpec  with WireMockFixture {

  val trackingIdValue = TrackingId("trackingIdValue")
  val interval = Interval(50.millis)

  implicit val noCookieFlags = new NoCookieFlags
  implicit val clientSideSessionFactory = new ClearTextClientSideSessionFactory()
  val addressLookupService = new WebServiceImpl(new TestOrdnanceSurveyConfig(wireMockPort))

  "postcodeWithNoSpaces" should {
    "return the same string if no spaces present" in {
      val result = addressLookupService.postcodeWithNoSpaces(PostcodeValid)

      result should equal(PostcodeValid)
    }

    "remove spaces when present" in {
      val result = addressLookupService.postcodeWithNoSpaces(PostcodeValidWithSpace)

      result should equal(PostcodeValid)
    }
  }

  "callPostcodeWebService" should {
    "send the trackingId to the PostcodeWebService" in new WithApplication {
      val postCode = "N193NN"

      val futureResult = addressLookupService.callPostcodeWebService(postCode, trackingIdValue, showBusinessName = None)(lang = Lang("en"))

      whenReady(futureResult, timeout, interval) { result =>
        wireMock.verifyThat(1, getRequestedFor(
          urlEqualTo(s"/postcode-to-address?postcode=$postCode&languageCode=EN&tracking_id=$trackingIdValue")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingIdValue.value)))
      }
    }

    "send the trackingId to the callUprnWebService" in new WithApplication {
      val postCode = "N193NN"

      val futureResult = addressLookupService.callUprnWebService(postCode, trackingIdValue)(Lang("en"))

      whenReady(futureResult, timeout, interval) { result =>
        wireMock.verifyThat(1, getRequestedFor(
          urlEqualTo(s"/uprn-to-address?uprn=$postCode&languageCode=EN&tracking_id=$trackingIdValue")
        ).withHeader(HttpHeaders.TrackingId, equalTo(trackingIdValue.value)))
      }
    }
  }
}

class TestOrdnanceSurveyConfig(wireMockPort: Int) extends OrdnanceSurveyConfig {
  override lazy val baseUrl = s"http://localhost:$wireMockPort"
  override lazy val requestTimeout = 5.seconds.toMillis.toInt

}
