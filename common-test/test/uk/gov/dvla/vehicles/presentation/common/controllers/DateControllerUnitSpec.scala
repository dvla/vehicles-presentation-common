package uk.gov.dvla.vehicles.presentation.common.controllers

import uk.gov.dvla.vehicles.presentation.common.helpers.TestWithApplication
import uk.gov.dvla.vehicles.presentation.common.helpers.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.models
import models.DateModel
import models.DateModel.Key
import org.joda.time.LocalDate
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, defaultAwaitTimeout}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{TrackingId, ClearTextClientSideSession}


class DateControllerUnitSpec extends UnitSpec {
  private def dateController = {
    injector.getInstance(classOf[DateController])
  }

  "Private keeper detail complete controller" should {
    "present an empty form" in new TestWithApplication {
      val request = FakeRequest()
      whenReady(dateController.present(request)) {
        r => r.header.status should equal(OK)
      }
    }

    "present a full form" in new TestWithApplication {
      val value = DateModel(new LocalDate(1234, 12, 24))

      val request = FakeRequest()
        .withCookies(createCookie(Key.value, value))
      val html = contentAsString(dateController.present(request))
      html should include("24")
      html should include("12")
      html should include("1234")
    }
  }

  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    import uk.gov.dvla.vehicles.presentation.common.clientsidesession.NoCookieFlags
    val session = new ClearTextClientSideSession(TrackingId("trackingId"))(new NoCookieFlags)
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }
}
