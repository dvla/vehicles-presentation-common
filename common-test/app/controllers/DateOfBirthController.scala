package controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory


class DateOfBirthController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends Controller {

  private[controllers] val form = Form(models.DateOfBirthModel.Form.Mapping)

  def present = Action { implicit request =>
    Ok(views.html.valtechDateOfBirthView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechDateOfBirthView(invalidForm)),
        validForm => {
          val msg = s"Success - you have correctly declared your intent to proceed"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
