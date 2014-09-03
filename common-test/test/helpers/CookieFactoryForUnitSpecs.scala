package helpers

import composition.TestComposition
import models.ValtechSelectModel
import models.ValtechSelectModel.ValtechSelectModelCacheKey
import models.ValtechRadioModel
import models.ValtechRadioModel.ValtechRadioModelCacheKey
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{ClearTextClientSideSession, CookieFlags}
import views.ValtechRadioView.KeeperType_Private

object CookieFactoryForUnitSpecs extends TestComposition {

  implicit private val cookieFlags = injector.getInstance(classOf[CookieFlags])
  final val TrackingIdValue = "trackingId"
  private val session = new ClearTextClientSideSession(TrackingIdValue)

  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }

  private def createCookie[A](key: String, value: String): Cookie = {
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, value)
  }

  def valtechRadio(keeperType: String = KeeperType_Private): Cookie = {
    val key = ValtechRadioModelCacheKey
    val value = ValtechRadioModel(keeperType = keeperType)
    createCookie(key, value)
  }

  def valtechSelect(selectedOption: String = ""): Cookie = {
    val key = ValtechSelectModelCacheKey
    val value = ValtechSelectModel(selectedInList = selectedOption)
    createCookie(key, value)
  }
}
