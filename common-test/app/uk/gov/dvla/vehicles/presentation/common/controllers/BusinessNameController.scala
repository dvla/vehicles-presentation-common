package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.views
import models.BusinessNameModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class BusinessNameController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    BusinessNameModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.businessNameView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.businessNameView(invalidForm)),
        validForm => {
          val msg = s"Success - you entered business name of ${validForm.name}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
