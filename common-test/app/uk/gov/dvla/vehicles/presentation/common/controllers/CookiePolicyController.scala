package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.CookieReport
import uk.gov.dvla.vehicles.presentation.common.views

class CookiePolicyController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  protected val cookies = List(
    CookieReport("_ga", "ga", "normal", "2years"),
    CookieReport("_gat", "gat", "normal", "10min"),
    CookieReport("tracking_id", "tracking_id", "normal", "7days"),
    CookieReport("PLAY_LANG", "PLAY_LANG", "normal", "close"),
    CookieReport("40 character length", "encrypted", "normal-secure", "7days")
  )

  def present = Action { implicit request =>
    Ok(views.html.cookiesPolicyPage(cookies))
  }
}
