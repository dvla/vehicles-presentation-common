package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.Play
import play.api.test.FakeRequest
import play.api.test.Helpers.{LOCATION, HOST, NOT_FOUND, REFERER, SEE_OTHER}
import uk.gov.dvla.vehicles.presentation.common
import common.{WithApplication, UnitSpec}
import common.controllers.AlternateLanguages.{withLanguage, CyId, EnId}
import common.testhelpers.CookieHelper.fetchCookiesFromHeaders

final class AlternateLanguagesUnitSpec extends UnitSpec {
  val initialPagePath = "/the/initial/page"
  val host = "testHost.com"
  val referer = s"https://$host$initialPagePath"

  "withLanguage" should {
    "prevent redirect to referrers outside our website" ignore new WithApplication {
      val result = withLanguage(CyId)(request.withHeaders(HOST -> "our.app", REFERER -> "http://external.referer"))
      whenReady(result) { r =>
        r.header.status should equal(NOT_FOUND)
      }
    }
  }

  "withLanguageCy" should {
    "redirect back to the same page" ignore new WithApplication {
      val result = withLanguage(CyId)(request)
      whenReady(result) { r =>
        r.header.status should equal(SEE_OTHER) // Redirect...
        r.header.headers.get(LOCATION) should equal(Some(initialPagePath)) // ... back to the same page.
      }
    }

    "writes language cookie set to 'cy'" ignore new WithApplication {
      val result = withLanguage(CyId)(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.find(_.name == Play.langCookieName) match {
          case Some(cookie) => cookie.value should equal("cy")
          case None => fail("langCookieName not found")
        }
      }
    }
  }

  "withLanguageEn" should {
    "redirect back to the same page" ignore new WithApplication {
      val result = withLanguage(EnId)(request)
      whenReady(result) { r =>
        r.header.status should equal(SEE_OTHER) // Redirect...
        r.header.headers.get(LOCATION) should equal(Some(initialPagePath)) // ... back to the same page.
      }
    }

    "writes language cookie set to 'en'" ignore new WithApplication {
      val result = withLanguage(EnId)(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.find(_.name == Play.langCookieName) match {
          case Some(cookie) => cookie.value should equal("en")
          case None => fail("langCookieName not found")
        }
      }
    }
  }

  private val request = FakeRequest().withHeaders(HOST -> host, REFERER -> referer)
}