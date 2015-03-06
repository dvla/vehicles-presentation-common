package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel
import uk.gov.dvla.vehicles.presentation.common.views

class OptionToggleController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    OptionalToggleModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.optionToggle(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.optionToggle(invalidForm)),
        validForm => {
          Ok(views.html.success("Works fire for the time being"))
//          validForm.mileage match {
//            case Some(mileage) =>
//              val msg = s"Success - you entered a mileage of $mileage"
//            case _ =>
//              val msg = s"Success - you entered a mileage of NOT ENTERED"
//              Ok(views.html.success(msg))
//          }
        }
      )
    }
  }

}
