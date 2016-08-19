package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.Controller
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.model.CookieReport

class CookiePolicy @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  protected val cookies = List(
    CookieReport("_ga", "ga", "normal", "2years"),
    CookieReport("_gat", "gat", "normal", "10min")
  )
}
