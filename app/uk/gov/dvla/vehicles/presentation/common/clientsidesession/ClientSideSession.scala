package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.mvc.Cookie

trait ClientSideSession {
  val trackingId: TrackingId

  def nameCookie(key: String): CookieName

  def newCookie(name: CookieName, value: String, key: String): Cookie

  def newCookie(name: CookieName, value: String): Cookie

  def getCookieValue(cookie: Cookie): String
}

// When modifying this class be sure to replicate the changes in
//    vehicles-services-common/src/main/scala/dvla.common/clientsidesession/ClientSideSession.scala
case class TrackingId(value: String) {
  override def toString:String = value
}