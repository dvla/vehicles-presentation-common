package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}

class Application @Inject() extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.index())
  }

}