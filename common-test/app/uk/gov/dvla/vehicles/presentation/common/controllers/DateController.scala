package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views

class DateController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends Controller {

  private[controllers] val form = Form(models.DateModel.Form.Mapping)

  def present = Action { implicit request =>
    Ok(views.html.valtechDateView(form.fill()))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechDateView(invalidForm)),
        validForm => {
          val msg = s"Success - you have correctly declared your intent to proceed"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
