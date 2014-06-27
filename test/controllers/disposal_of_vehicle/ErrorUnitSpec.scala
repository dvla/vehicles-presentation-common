package controllers.disposal_of_vehicle

import common.ClientSideSessionFactory
import helpers.common.CookieHelper
import CookieHelper._
import helpers.disposal_of_vehicle.CookieFactoryForUnitSpecs
import helpers.{UnitSpec, WithApplication}
import org.mockito.Mockito._
import pages.disposal_of_vehicle.ErrorPage
import play.api.Play
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.helpers.Config

final class ErrorUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new WithApplication {
      whenReady(present) {
        r => r.header.status should equal(OK)
      }
    }

    "not display progress bar" in new WithApplication {
      contentAsString(present) should not include "Step "
    }

    "display prototype message when config set to true" in new WithApplication {
      contentAsString(present) should include("""<div class="prototype">""")
    }

    "not display prototype message when config set to false" in new WithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.isPrototypeBannerVisible).thenReturn(false)
      val errorPrototypeNotVisible = new Error()

      val result = errorPrototypeNotVisible.present(ErrorPage.exceptionDigest)(request)
      contentAsString(result) should not include """<div class="prototype">"""
    }
  }

  // TODO please add test for 'submit'.


  private val errorController = injector.getInstance(classOf[Error])

  private lazy val present = {
    val request = FakeRequest().
      withCookies(CookieFactoryForUnitSpecs.setupTradeDetails()).
      withCookies(CookieFactoryForUnitSpecs.traderDetailsModel()).
      withCookies(CookieFactoryForUnitSpecs.vehicleDetailsModel())
    errorController.present(ErrorPage.exceptionDigest)(request)
  }
}
