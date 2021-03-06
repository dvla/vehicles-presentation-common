package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views
import models.ValtechInputDigitsModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class ValtechInputDigitsController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    ValtechInputDigitsModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.valtechInputDigitsView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechInputDigitsView(invalidForm)),
        validForm => {
          val msg = s"Success - you entered a mileage of ${validForm.mileage}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
