package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import models.ValtechDeclareCheckModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class ValtechDeclareCheckController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    ValtechDeclareCheckModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.valtechDeclareCheckView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechDeclareCheckView(invalidForm)),
        validForm => {
          val msg = s"Success - you have correctly declared your intent to proceed"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}