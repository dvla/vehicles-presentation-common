package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import models.ValtechInputTextModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class ValtechInputTextController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    ValtechInputTextModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.valtechInputTextView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechInputTextView(invalidForm)),
        validForm => {
          val msg = s"Success - you entered value of ${validForm.inputText}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
