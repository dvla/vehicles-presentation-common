package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.mvc.Cookie

trait ClientSideSessionFactory {
  def getSession(request: Traversable[Cookie]): ClientSideSession

  def newSessionCookiesIfNeeded(request: Traversable[Cookie]): Option[Seq[Cookie]]
}

object ClientSideSessionFactory {
  final val TrackingIdCookieName = "tracking_id"
}