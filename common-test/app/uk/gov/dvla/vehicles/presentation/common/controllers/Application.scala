package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.views

class Application @Inject() extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.index())
  }
}
