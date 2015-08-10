package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.i18n.Lang
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import play.api.Play.current

class AlternateLanguages @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends Controller with DVLALogger {

  def withLanguage(chosenLanguage: String) = Action { implicit request =>
    val refererOpt = request.headers.get(REFERER)
    val safeReferer = refererOpt.filter { _.stripPrefix("https://").stripPrefix("http://") startsWith request.host }

    logMessage(request.cookies.trackingId(), Debug, s"referrer $refererOpt host ${request.host}")

    safeReferer.map { ref =>
      Redirect(ref).withLang(Lang(chosenLanguage))
    } getOrElse NotFound("The link is invalid")
  }
}


object AlternateLanguages {

  final val CyId = "cy"
  final val EnId = "en"
  val langCy = Lang(CyId)
  val langEn = Lang(EnId)

}
