package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import models.EmailModel


class EmailController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    EmailModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.emailView(form.fill()))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.emailView(formWithErrors)),
        f => Ok(views.html.success(s"success - you entered an email of ${f.email}"))
      )
    }
  }
}
