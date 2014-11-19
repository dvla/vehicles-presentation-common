package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views
import models.MileageModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class MileageController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    MileageModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.mileageView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.mileageView(invalidForm)),
        validForm => {
          validForm.mileage match {
            case Some(mileage) =>
              val msg = s"Success - you entered a mileage of $mileage"
              Ok(views.html.success(msg))
            case _ =>
              val msg = s"Success - you entered a mileage of NOT ENTERED"
              Ok(views.html.success(msg))
          }
        }
      )
    }
  }
}
