package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.Controller
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.model.CookieReport

class CookiePolicy @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  protected val cookies = Map(
    "google_analytics" -> List(
      CookieReport("_utma", "utma", "2years"),
      CookieReport("_utmb", "utmb", "30min"),
      CookieReport("_utmc", "utmc", "close"),
      CookieReport("_utmz", "utmz", "6months"),
      CookieReport("ga_nextpage_params", "ga_nextpage_params","close"),
      CookieReport("GDS_successEvents and GDS_analyticsTokens", "GDS_successEvents", "4months")
    ),
    "sessions" -> List(
      CookieReport("mdtp", "mdtp", "inactivity")
    ),
    "introductory_message" -> List(
      CookieReport("seen_cookie_message", "seen_cookie_message", "1month")
    ),
    "language" -> List(
      CookieReport("PLAY_LANG", "PLAY_LANG", "1year")
    )
  )
}