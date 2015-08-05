package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.mvc.Cookie
import play.api.Play
import play.api.test.FakeRequest
import play.api.test.Helpers.{LOCATION, HOST, NOT_FOUND, REFERER, SEE_OTHER}
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.NoCookieFlags

import common.testhelpers.CookieHelper.fetchCookiesFromHeaders
import common.{UnitSpec, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSessionFactory, ClientSideSessionFactory}

final class AlternateLanguagesUnitSpec extends UnitSpec {
  val host = "testHost.com"
  val referer = s"https://$host/the/initial/page"
  final val CyId = "cy"
  final val EnId = "en"
  implicit val nooCookieFlag = new NoCookieFlags()
  implicit val clientSideSessionFactory: ClientSideSessionFactory = new ClearTextClientSideSessionFactory
  val testLang = new AlternateLanguages()

  "withLanguage" should {
    "prevent redirect to referers outside our website for http" in new WithApplication {
      val result = testLang.withLanguage(CyId)(request.withHeaders(HOST -> "our.app", REFERER -> "http://external.referer"))
      whenReady(result) { r =>
        r.header.status should equal(NOT_FOUND)
      }
    }

    "prevent redirect to referers outside our website for https" in new WithApplication {
      val result = testLang.withLanguage(CyId)(request.withHeaders(HOST -> "our.app", REFERER -> "https://external.referer"))
      whenReady(result) { r =>
        r.header.status should equal(NOT_FOUND)
      }
    }
  }

  "withLanguageCy" should {
    "redirect back to the same page" in new WithApplication {
      val result = testLang.withLanguage(CyId)(request)
      whenReady(result) { r =>
        r.header.status should equal(SEE_OTHER) // Redirect...
        r.header.headers.get(LOCATION) should equal(Some(referer)) // ... back to the same page.
      }
    }

    "writes language cookie set to 'cy'" in new WithApplication {
      val result = testLang.withLanguage(CyId)(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies should contain (Cookie(Play.langCookieName, "cy"))
      }
    }
  }

  "withLanguageEn" should {
    "redirect back to the same page" in new WithApplication {
      val result = testLang.withLanguage(EnId)(request)
      whenReady(result) { r =>
        r.header.status should equal(SEE_OTHER) // Redirect...
        r.header.headers.get(LOCATION) should equal(Some(referer)) // ... back to the same page.
      }
    }

    "writes language cookie set to 'en'" in new WithApplication {
      val result = testLang.withLanguage(EnId)(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies should contain (Cookie(Play.langCookieName, "en"))
      }
    }
  }

  private val request = FakeRequest().withHeaders(HOST -> host, REFERER -> referer)
}