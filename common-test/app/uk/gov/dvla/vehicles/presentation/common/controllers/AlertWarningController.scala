package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views

class AlertWarningController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.alertWarningView())
  }
}
