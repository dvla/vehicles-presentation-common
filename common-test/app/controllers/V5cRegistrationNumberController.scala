package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import play.api.data.Forms.mapping
import models.V5cRegistrationNumberModel
import models.V5cRegistrationNumberModel.Form.{V5CRegistrationNumber, v5cRegistrationNumberID}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class V5cRegistrationNumberController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  val v5cRegistrationNumberForm = Form(
    mapping(
      v5cRegistrationNumberID -> V5CRegistrationNumber(minLength = 2, maxLength = 7)
    )(V5cRegistrationNumberModel.apply)(V5cRegistrationNumberModel.unapply)
  )

  def present = Action { implicit request =>
      Ok(views.html.v5cRegistrationNumberView(v5cRegistrationNumberForm))
  }

  def submit = Action {
    implicit request => {
      v5cRegistrationNumberForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.v5cRegistrationNumberView(formWithErrors)),
        f => Ok(views.html.success(s"success - you entered v5cRegistrationNumber of ${f.v5cRegistrationNumber}"))
      )
    }
  }
}
