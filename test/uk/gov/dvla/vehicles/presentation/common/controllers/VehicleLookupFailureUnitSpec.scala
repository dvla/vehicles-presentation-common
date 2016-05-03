package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.OK
import uk.gov.dvla.vehicles.presentation.common
import common.{TestWithApplication, UnitSpec}
import common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import common.model.CacheKeyPrefix
import common.testhelpers.CookieFactoryForUnitSpecs.{bruteForcePreventionCookie, vehicleLookupFormModel, vehicleLookupResponseCode}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class VehicleLookupFailureUnitSpec extends UnitSpec {

  implicit val cookieFlags = new NoCookieFlags()
  implicit val clientSideSessionFactory = new ClearTextClientSideSessionFactory()
  implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")

  private def controller = new VehicleLookupFailureTesting()

  import VehicleLookupFailureTesting._

  "present" should {
    "display the page" in new TestWithApplication {
      val vehicleLookupFailure = controller
      val request = FakeRequest()
        .withCookies(bruteForcePreventionCookie())
        .withCookies(vehicleLookupFormModel())
        .withCookies(vehicleLookupResponseCode())
      val result = Await.result(vehicleLookupFailure.present(request), 5 seconds)

      result.header.status should equal(OK)
      result.body should equal(presentTestResult.body)

    }

    "handle when the brute force prevention cookie is missing" in new TestWithApplication {
      val vehicleLookupFailure = controller
      val request = FakeRequest()
        .withCookies(vehicleLookupFormModel())
        .withCookies(vehicleLookupResponseCode())
      val result = Await.result(vehicleLookupFailure.present(request), 5 seconds)
      result should equal(missingPresentCookieDataTestResult)
    }

    "handle when the vehicle lookup form model cookie is missing" in new TestWithApplication {
      val vehicleLookupFailure = controller
      val request = FakeRequest()
        .withCookies(bruteForcePreventionCookie())
        .withCookies(vehicleLookupResponseCode())
      val result = Await.result(vehicleLookupFailure.present(request), 5 seconds)
      result should equal(missingPresentCookieDataTestResult)
    }

  }

  "submit" should {
    "display the page" in new TestWithApplication {
      val vehicleLookupFailure = controller
      val request = FakeRequest()
        .withCookies(vehicleLookupFormModel())
      val result = Await.result(vehicleLookupFailure.submit(request), 5 seconds)
      result should equal(submitTestResult)
    }

    "handle when the cookie data is missing" in new TestWithApplication {
      val vehicleLookupFailure = controller
      val result = Await.result(vehicleLookupFailure.submit(FakeRequest()), 5 seconds)
      result should equal(missingSubmitCookieDataTestResult)
    }
  }
}
