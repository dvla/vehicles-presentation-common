package uk.gov.dvla.vehicles.presentation.common.controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.HeaderNames
import play.api.libs.json.DefaultWrites
import play.api.mvc.{Results, Request, Call}
import play.api.test.FakeRequest
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import uk.gov.dvla.vehicles.presentation.common
import common.UnitSpec
import common.clientsidesession.{CacheKey, ClearTextClientSideSessionFactory, NoCookieFlags}
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.controllers.VehicleLookupBase.LookupResult
import common.model.{CacheKeyPrefix, BruteForcePreventionModel}
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import org.mockito.Mockito.{verify, when, never, reset}

class VehicleLookupBaseSpec extends UnitSpec with DefaultWrites with BeforeAndAfterEach {
  val mockBruteService = mock[BruteForcePreventionService]
  val vehicleLookupFailureCall =  Call("GET", "http://www.gov.uk/lookupFailure")
  val microServiceErrorCall =  Call("GET", "http://www.gov.uk/serviceError")
  val vrmLockedCall =  Call("GET", "http://www.gov.uk/vrmLocked")
  val mockForm = "this is some random value"
  val success = Results.Status(12345)
  val responseCodeCacheKey = "responseCodeCacheKey"

  val vrm = "A11"
  val refNum = "12345678901"
  val responseCode = "vehicle not found"

  implicit val request = FakeRequest()
  implicit val Key = CacheKey[String]("cachekey")
  implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")
  implicit val sessionFactory = new ClearTextClientSideSessionFactory()(new NoCookieFlags)

  override protected def beforeEach() = {
    reset(mockBruteService)
  }

  "brute force and lookup" should {

    "redirect to the result of the overridden callLookupService method if not locked" in {
      val bruteForcePreventionModel = BruteForcePreventionModel(permitted = true, 1, 3, "")
      when(mockBruteService.isVrmLookupPermitted(vrm)).thenReturn(
        Future.successful(bruteForcePreventionModel)
      )
      when(mockBruteService.reset(vrm)).thenReturn(Future.successful(1))
      val resultFuture = new VehicleLookupBaseTest(
        mockBruteService, responseCodeCacheKey, VehicleLookupBase.VehicleFound(success)
      ).bruteForceAndLookup(vrm, refNum, mockForm)

      val result = Await.result(resultFuture, 5000 millis)
      result.cookies.get(Key.value).get.value should include(mockForm)
      result.cookies.get(responseCodeCacheKey) should be(None)
      result.header.status should be(12345)

      RichCookies(result.cookies.values.toList)
        .getModel[BruteForcePreventionModel] should be(Some(bruteForcePreventionModel))
      verify(mockBruteService).isVrmLookupPermitted(vrm)
      verify(mockBruteService).reset(vrm)
    }

    "redirect to the failure page on vehicle lookup failure" in {
      val bruteForcePreventionModel = BruteForcePreventionModel(permitted = true, 1, 3, "")
      when(mockBruteService.isVrmLookupPermitted(vrm)).thenReturn(
        Future.successful(bruteForcePreventionModel)
      )
      when(mockBruteService.reset(vrm)).thenReturn(Future.successful(1))

      val resultFuture = new VehicleLookupBaseTest(
        mockBruteService, responseCodeCacheKey, VehicleLookupBase.VehicleNotFound(responseCode)
      ).bruteForceAndLookup(vrm, refNum, mockForm)

      val result = Await.result(resultFuture, 5000 millis)

      println("headers.Location: " + result.header.headers)

      result.header.status should be(303)
      result.cookies.get(Key.value).get.value should include(mockForm)
      result.cookies.get(responseCodeCacheKey).get.value should include(responseCode)
      result.header.headers.get(HeaderNames.LOCATION).get should equal(vehicleLookupFailureCall.url)

      RichCookies(result.cookies.values.toList)
        .getModel[BruteForcePreventionModel] should be(Some(bruteForcePreventionModel))

      verify(mockBruteService).isVrmLookupPermitted(vrm)
      verify(mockBruteService, never).reset(vrm)
    }

    "redirect to the microservice error page in case of microservice error" in {
      when(mockBruteService.isVrmLookupPermitted(vrm)).thenReturn(Future.failed(new RuntimeException()))
      when(mockBruteService.reset(vrm)).thenReturn(Future.successful(1))

      val resultFuture = new VehicleLookupBaseTest(
        mockBruteService, responseCodeCacheKey, VehicleLookupBase.VehicleNotFound(responseCode)
      ).bruteForceAndLookup(vrm, refNum, mockForm)

      val result = Await.result(resultFuture, 5000 millis)

      result.header.status should be(303)
      result.cookies.get(Key.value).get.value should include(mockForm)
      result.cookies.get(responseCodeCacheKey) should be(None)
      result.header.headers.get(HeaderNames.LOCATION).get should equal(microServiceErrorCall.url)

      RichCookies(result.cookies.values.toList)
        .getModel[BruteForcePreventionModel] should be(None)

      verify(mockBruteService).isVrmLookupPermitted(vrm)
      verify(mockBruteService, never).reset(vrm)
    }

    "redirect to the locked page when the vrm is locked" in {
      val bruteForcePreventionModel = BruteForcePreventionModel(permitted = false, 4, 3, "")
      when(mockBruteService.isVrmLookupPermitted(vrm)).thenReturn(
        Future.successful(bruteForcePreventionModel)
      )
      when(mockBruteService.reset(vrm)).thenReturn(Future.successful(1))

      val resultFuture = new VehicleLookupBaseTest(
        mockBruteService, responseCodeCacheKey, VehicleLookupBase.VehicleNotFound(responseCode)
      ).bruteForceAndLookup(vrm, refNum, mockForm)

      val result = Await.result(resultFuture, 5000 millis)

      result.header.status should be(303)
      result.cookies.get(Key.value).get.value should include(mockForm)
      result.cookies.get(responseCodeCacheKey) should be(None)
      result.header.headers.get(HeaderNames.LOCATION).get should equal(vrmLockedCall.url)

      RichCookies(result.cookies.values.toList)
        .getModel[BruteForcePreventionModel] should be(Some(bruteForcePreventionModel))

      verify(mockBruteService).isVrmLookupPermitted(vrm)
      verify(mockBruteService, never).reset(vrm)
    }
  }

  private class VehicleLookupBaseTest(override val bruteForceService: BruteForcePreventionService,
                                      override val responseCodeCacheKey: String,
                                      lookupResult: LookupResult,
                                      override val vrmLocked: Call = vrmLockedCall,
                                      override val vehicleLookupFailure: Call = vehicleLookupFailureCall,
                                      override val microServiceError: Call = microServiceErrorCall) extends VehicleLookupBase {
    override type Form = String
    override implicit val clientSideSessionFactory = sessionFactory

    protected def callLookupService(trackingId: String, form: Form)(implicit request: Request[_]): Future[LookupResult] = {
      if (lookupResult == null) throw new Exception

      Future.successful(lookupResult)
    }

  }
}
