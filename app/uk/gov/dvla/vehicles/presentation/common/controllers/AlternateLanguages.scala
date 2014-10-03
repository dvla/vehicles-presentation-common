package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.Play.current
import play.api.i18n.Lang
import play.api.mvc.{Action, Controller, Request}

object AlternateLanguages extends Controller {
  final val CyId = "cy"
  final val EnId = "en"
  val langCy = Lang(CyId)
  val langEn = Lang(EnId)

  def withLanguage(chosenLanguage: String) = Action { implicit request =>
    val referer = request.headers.get(REFERER)
    val safeReferer = referer.filter(_.startsWith(protocolAndHost(request)))

    safeReferer.map { ref =>
      Redirect(ref).withLang(Lang(chosenLanguage))
    } getOrElse BadRequest("The link is invalid")
  }

  def protocolAndHost(request: Request[_]) = "http" + (if (request.secure) "s" else "") + "://" + request.host
}
