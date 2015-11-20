package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Call, Action, Controller}
import play.core.Router.ReverseRouteContext
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.filters.CsrfPreventionAction.CsrfPreventionToken
import common.views

class MicroServiceErrorController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  implicit val token: uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.CsrfPreventionToken =
    new CsrfPreventionToken("123")

  val tryAgainTarget = fakeCall()
  val exitTarget = fakeCall()

  def fakeCall(): Call = {
    import ReverseRouteContext.empty
    Call("POST", "")
  }

  def present = Action { implicit request =>
    val trackingId = request.cookies.trackingId()
    Ok(views.html.microServiceErrorView(tryAgainTarget, exitTarget, trackingId))
  }
}
