package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import models.TitlePickerModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class TitlePickerController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    TitlePickerModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.titlePickerView(form.fill()))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.titlePickerView(invalidForm)),
        validForm => {
          val msg = s"Success - you selected a title of ${validForm.title}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
