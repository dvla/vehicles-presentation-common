package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import com.google.inject.Inject
import play.api.mvc.Cookie

class ClearTextClientSideSessionFactory @Inject()(implicit cookieFlags: CookieFlags) extends ClientSideSessionFactory {

  override def newSessionCookiesIfNeeded(request: Traversable[Cookie]): Option[Seq[Cookie]] = None

  override def getSession(request: Traversable[Cookie]): ClientSideSession =
    getTrackingId(request) match {
      case Some(trackingId) => new ClearTextClientSideSession(TrackingId(trackingId))
      case None => new ClearTextClientSideSession(ClearTextClientSideSessionFactory.DefaultTrackingId)
    }

  private def getTrackingId(request: Traversable[Cookie]): Option[String] =
    request.find(_.name == ClientSideSessionFactory.TrackingIdCookieName).map(_.value)
}

object ClearTextClientSideSessionFactory {
  final val DefaultTrackingId = TrackingId("default_test_tracking_id")
}