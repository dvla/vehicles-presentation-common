package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.test.{FakeRequest, WithApplication}
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import common.model.CacheKeyPrefix
import common.testhelpers.CookieFactoryForUnitSpecs.bruteForcePreventionCookie
import common.testhelpers.CookieFactoryForUnitSpecs.defaultBruteForcePreventionModel
import common.UnitSpec

class VrmLockedBaseUnitSpec extends UnitSpec {

  implicit val cookieFlags = new NoCookieFlags()
  implicit val clientSideSessionFactory = new ClearTextClientSideSessionFactory()
  implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")

  private def controller = new VrmLockedTesting()

  import VrmLockedTesting._

  "present" should {
    "display the page" in new WithApplication {
      val vrmLocked = controller
      val request = FakeRequest().withCookies(bruteForcePreventionCookie())
      val result = Await.result(vrmLocked.present(request), 5 seconds)
      result should equal(presentTestResult)
      vrmLocked.presentResultArgs should equal(Seq(defaultBruteForcePreventionModel))
    }

    "handle when the brute force prevention cookie is missing" in new WithApplication {
      val vrmLocked = controller
      val result = Await.result(vrmLocked.present(FakeRequest()), 5 seconds)
      result should equal(missingBruteForcePreventionCookieTestResult)
    }
  }

  "tryAgain" should {
    "display the page" in new WithApplication {
      val vrmLocked = controller
      val result = Await.result(vrmLocked.tryAgain(FakeRequest()), 5 seconds)
      result should equal(tryAgainTestResult)
    }
  }

  "exit" should {
    "display the page" in new WithApplication {
      val vrmLocked = controller
      val result = Await.result(vrmLocked.exit(FakeRequest()), 5 seconds)
      result should equal(exitTestResult)
    }
  }
}
