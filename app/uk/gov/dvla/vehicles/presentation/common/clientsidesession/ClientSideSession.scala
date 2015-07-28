package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.mvc.Cookie

trait ClientSideSession {
  val trackingId: TrackingId

  def nameCookie(key: String): CookieName

  def newCookie(name: CookieName, value: String, key: String): Cookie

  def newCookie(name: CookieName, value: String): Cookie

  def getCookieValue(cookie: Cookie): String
}

case class TrackingId(value: String)