package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import models.PostcodeModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class PostcodeController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    PostcodeModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.postcodeView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.postcodeView(formWithErrors)),
        f => Ok(views.html.success(s"success - you entered a postcode of ${f.postcode}"))
      )
    }
  }
}