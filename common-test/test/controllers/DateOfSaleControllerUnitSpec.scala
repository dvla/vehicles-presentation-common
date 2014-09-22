package controllers

import helpers.{UnitSpec, WithApplication}
import models.DateModel
import models.DateModel.Key
import org.joda.time.LocalDate
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, defaultAwaitTimeout}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClearTextClientSideSession


class DateOfSaleControllerUnitSpec extends UnitSpec {
  private val nonFutureDateController = {
    injector.getInstance(classOf[DateOfBirthController])
  }

  "present" should {
    "show an empty form when no cookie are found" in new WithApplication {
      val request = FakeRequest()
      whenReady(nonFutureDateController.present(request)) {
        r => r.header.status should equal(OK)
      }
    }

    "show a full form when cookie is found" in new WithApplication {
      val stubbedDate = new LocalDate(1234, 12, 24)
      val value = DateModel(optionalDate = Some(stubbedDate), date = stubbedDate)

      val request = FakeRequest()
        .withCookies(createCookie(Key.value, value))
      val html = contentAsString(nonFutureDateController.present(request))
      html should include("24")
      html should include("12")
      html should include("1234")
    }
  }

  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    import uk.gov.dvla.vehicles.presentation.common.clientsidesession.NoCookieFlags
    val session = new ClearTextClientSideSession("trackingId")(new NoCookieFlags)
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }
}
