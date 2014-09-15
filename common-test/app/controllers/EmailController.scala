package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import models.EmailModel

class EmailController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    EmailModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.emailView(form))
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