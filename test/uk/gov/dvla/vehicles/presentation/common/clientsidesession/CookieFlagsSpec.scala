package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common.{TestWithApplication, TestGlobalSettings, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication
import scala.concurrent.duration.DurationInt

class CookieFlagsSpec extends UnitSpec {
  "CookieFlagsFromConfig" should {
    "return a cookie with max age and secure properties set" in new TestWithApplication(testApp = fakeAppWithCookieConfig) {
      val cookieFlags = new CookieFlagsFromConfig
      val originalCookie = Cookie(name = "testCookieName", value = "testCookieValue")

      originalCookie.secure should equal(false)
      originalCookie.maxAge should equal(None)

      // This will load values from the fake config we are passing into this test's WithApplication.
      val modifiedCookie = cookieFlags.applyToCookie(originalCookie)
      modifiedCookie.secure should equal(true)
      modifiedCookie.maxAge should equal(Some(TenMinutesInSeconds))
    }
  }

  private final val TenMinutesInSeconds = 10.minutes.toSeconds.toInt

  private val fakeAppWithCookieConfig = LightFakeApplication(
    TestGlobalSettings,
    Map("secureCookies" -> true, "application.cookieMaxAge" -> TenMinutesInSeconds)
  )
}