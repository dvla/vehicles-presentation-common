package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.i18n.Lang
import play.api.Logger
import play.api.mvc.{Action, Controller}
import play.api.Play.current

object AlternateLanguages extends Controller {
  final val CyId = "cy"
  final val EnId = "en"
  val langCy = Lang(CyId)
  val langEn = Lang(EnId)

  def withLanguage(chosenLanguage: String) = Action { implicit request =>
    val refererOpt = request.headers.get(REFERER)
    val safeReferer = refererOpt.filter { _.stripPrefix("https://").stripPrefix("http://") startsWith request.host }

    Logger.debug(s"referrer $refererOpt host ${request.host}")

    safeReferer.map { ref =>
      Redirect(ref).withLang(Lang(chosenLanguage))
    } getOrElse NotFound("The link is invalid")
  }
}
