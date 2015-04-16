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

  def jsTest = Action { implicit request =>
    Ok(views.html.optionToggle(form, true))
  }

  def present = Action { implicit request =>
    Ok(views.html.optionToggle(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.optionToggle(invalidForm)),
        validModel => {
          Ok(views.html.success(s"I got text: ${validModel.text} num:${validModel.num} date:${validModel.date}"))
        }
      )
    }
  }

}
