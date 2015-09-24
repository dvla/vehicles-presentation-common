package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import models.ValidateHtml5ClientSideModel

class ValidateHTML5ClientSide @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    ValidateHtml5ClientSideModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.validateHtml5ClientSide(form.fill()))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.validateHtml5ClientSide(formWithErrors)),
        f => Ok(views.html.success(s"success - you entered an email ${f.email}"))
      )
    }
  }
}
