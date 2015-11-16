package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Call, Action, Controller}
import play.core.Router.ReverseRouteContext
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.CsrfPreventionToken
import uk.gov.dvla.vehicles.presentation.common.views

class FormStepsController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  implicit val token: uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.CsrfPreventionToken =
    new CsrfPreventionToken("123")

  protected val newSaleFormTarget = fakeCall()
  protected val exitFormTarget = fakeCall()
  final val NewSaleId = "newDisposal"
  final val ExitId = "exitDisposal"
  final val surveyUrl = None

  def fakeCall(): Call = {
    import ReverseRouteContext.empty
    Call("POST", "")
  }

  def present = Action { implicit request =>
    Ok(views.html.formStepsView(hideNewSaleButton = false, newSaleFormTarget, NewSaleId, exitFormTarget, ExitId, surveyUrl))
  }
}