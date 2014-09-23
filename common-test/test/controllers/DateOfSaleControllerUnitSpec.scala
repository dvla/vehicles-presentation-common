package controllers

import helpers.{UnitSpec, WithApplication}
import models.DateModel
import models.DateModel.Key
import org.joda.time.LocalDate
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClearTextClientSideSession
import play.api.mvc.Cookie
import scala.Some


class DateOfSaleControllerUnitSpec extends UnitSpec {
  private val dateOfSaleController = {
    injector.getInstance(classOf[DateOfSaleController])
  }

  "present" should {
    "show an empty form when no cookie are found" in new WithApplication {
      val request = FakeRequest()
      whenReady(dateOfSaleController.present(request)) {
        r => r.header.status should equal(OK)
      }
    }

    "show a full form when cookie is found" in new WithApplication {
      val value = DateModel(Some(new LocalDate(1234, 12, 24)), new LocalDate(1234, 12, 24))

      val request = FakeRequest()
        .withCookies(createCookie(Key.value, value))
      val html = contentAsString(dateOfSaleController.present(request))
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
