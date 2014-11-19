package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views
import models.ValtechRadioModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class ValtechRadioController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    ValtechRadioModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.valtechRadioView(form.fill()))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechRadioView(invalidForm)),
        validForm => {
          val msg = s"Success - you selected a keeper type of ${validForm.keeperType}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
