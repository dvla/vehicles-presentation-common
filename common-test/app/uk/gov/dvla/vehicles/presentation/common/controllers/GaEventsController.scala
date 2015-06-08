package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.views

class GaEventsController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {
  private[controllers] val form = Form(
    mapping(
      "optionalString" -> optional(text()),
      "js-events" -> optional(text())
    )(GaModel.apply)(GaModel.unapply)
  )

  def present = Action { implicit request =>
    Ok(views.html.gaView(form))
  }


  def submit = Action { implicit request =>
    form.bindFromRequest().fold (
      invalidForm => BadRequest("the form was invalid. This will never happen as both fields are optional"),
      validModel => validModel match { case GaModel(text, jsEvents) =>
        Ok("Submit was called with jsEvents:" + jsEvents)
      }
    )
  }
}

case class GaModel(text: Option[String],jsEvents: Option[String])



