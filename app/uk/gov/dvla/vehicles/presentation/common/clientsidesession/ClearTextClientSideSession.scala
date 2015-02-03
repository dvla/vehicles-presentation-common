package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.mvc.Cookie

class ClearTextClientSideSession(override val trackingId: String)
                                (implicit cookieFlags: CookieFlags) extends ClientSideSession {

  override def nameCookie(key: String): CookieName = CookieName(key)

  override def newCookie(name: CookieName, value: String, key: String): Cookie =
    cookieFlags.applyToCookie(Cookie(name.value, value), key = key)

  override def newCookie(name: CookieName, value: String): Cookie = newCookie(name, value, key = "")

  override def getCookieValue(cookie: Cookie): String = cookie.value
}