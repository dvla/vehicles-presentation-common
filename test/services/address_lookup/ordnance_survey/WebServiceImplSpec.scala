package services.address_lookup.ordnance_survey

import helpers.UnitSpec
import services.fakes.FakeAddressLookupService._
import utils.helpers.Config
import org.scalatest.BeforeAndAfterEach
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.github.tomakehurst.wiremock.http.{Response, Request, RequestListener}
import common.{NoCookieFlags, ClearTextClientSideSession, ClientSideSession, ClientSideSessionFactory}
import scala.collection.mutable

final class WebServiceImplSpec extends UnitSpec with BeforeAndAfterEach {

  val wireMockPort = 36745
  val wireMockServer = new WireMockServer(wireMockConfig().port(wireMockPort))
  val trackingIdValue = "trackingIdValue"

  implicit val noCookieFlags = new NoCookieFlags
  implicit val clientSideSession: ClientSideSession = new ClearTextClientSideSession(trackingIdValue)

  override def beforeEach() {
    wireMockServer.start()
  }

  override def afterEach() {
    wireMockServer.stop()
  }

  import composition.TestComposition.{testInjector => injector}
  implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
  val addressLookupService = new services.address_lookup.ordnance_survey.WebServiceImpl(new Config() {
    override val ordnanceSurveyMicroServiceUrl = s"http://localhost:$wireMockPort"
  })

  "postcodeWithNoSpaces" should {

    "return the same string if no spaces present" in {
      val result = addressLookupService.postcodeWithNoSpaces(postcodeValid)

      result should equal(postcodeValid)
    }

    "remove spaces when present" in {
      val result = addressLookupService.postcodeWithNoSpaces(postcodeValidWithSpace)

      result should equal(postcodeValid)
    }
  }

  "WebServiceImplSpec" should {
    "send the trackingId to the PostcodeWebService" in {
      val sentRequestsUrls = addRequestListener()

      val postCode = "N193NN"

      val futureResult = addressLookupService.callPostcodeWebService(postCode)(Some(clientSideSession))

      whenReady(futureResult) { result =>
        sentRequestsUrls should have size 1
        println(sentRequestsUrls(0))
        sentRequestsUrls(0) should include(s"?postcode=$postCode")
        sentRequestsUrls(0) should include(s"&tracking-id=$trackingIdValue")
      }
    }

    "don't send the trackingId to the PostcodeWebService" in {
      val sentRequestsUrls = addRequestListener()

      val postCode = "N193NN"

      val futureResult = addressLookupService.callPostcodeWebService(postCode)(None)

      whenReady(futureResult) { result =>
        sentRequestsUrls should have size 1
        println(sentRequestsUrls(0))
        sentRequestsUrls(0) should include(s"?postcode=$postCode")
        sentRequestsUrls(0) should not include(s"&tracking-id=$trackingIdValue")
      }
    }

    "send the trackingId to the callUprnWebService" in {
      val sentRequestsUrls = addRequestListener()

      val postCode = "N193NN"

      val futureResult = addressLookupService.callUprnWebService(postCode)(Some(clientSideSession))

      whenReady(futureResult) { result =>
        sentRequestsUrls should have size 1
        println(sentRequestsUrls(0))
        sentRequestsUrls(0) should include(s"?uprn=$postCode")
        sentRequestsUrls(0) should include(s"&tracking-id=$trackingIdValue")
      }
    }
  }

  private def addRequestListener(): mutable.ArrayBuffer[String] =  {
    var sentRequestsUrls: mutable.ArrayBuffer[String] =  mutable.ArrayBuffer.empty[String]

    wireMockServer.addMockServiceRequestListener(new RequestListener(){
      override def requestReceived(request: Request, response: Response): Unit = {
        println(request.getUrl)
        sentRequestsUrls += request.getUrl
      }
    })

    sentRequestsUrls
  }
}
