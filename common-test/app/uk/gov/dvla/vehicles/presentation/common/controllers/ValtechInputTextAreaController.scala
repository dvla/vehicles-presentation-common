package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.models.ValtechInputTextAreaModel
import uk.gov.dvla.vehicles.presentation.common.views

class ValtechInputTextAreaController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    ValtechInputTextAreaModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.valtechInputTextAreaView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechInputTextAreaView(invalidForm)),
        validForm => {
          val msg = s"Success - you entered value of ${validForm.inputText}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
