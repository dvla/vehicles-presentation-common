package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.CookieReport
import uk.gov.dvla.vehicles.presentation.common.views

class CookiePolicyController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private val cookies = Map(
    "google_analytics" -> List(
      CookieReport("_utma",	"utma",	"2years"),
      CookieReport("_utmb",	"utmb",	"30min"),
      CookieReport("_utmc",	"utmc",	"close"),
      CookieReport("_utmz",	"utmz",	"6months"),
      CookieReport("ga_nextpage_params",	"ga_nextpage_params",	"close"),
      CookieReport("GDS_successEvents and GDS_analyticsTokens",	"GDS_successEvents",	"4months")
    ),
    "sessions" -> List(
      CookieReport("mdtp",	"mdtp",	"inactivity")
    ),
    "introductory_message" -> List(
      CookieReport("seen_cookie_message",	"seen_cookie_message",	"1month")
    )
  )

  def present = Action { implicit request =>
    Ok(views.html.cookiesPolicyPage(cookies))
  }
}
