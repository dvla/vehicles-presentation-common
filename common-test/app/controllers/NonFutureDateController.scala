package controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm


class NonFutureDateController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends Controller {

  private[controllers] val form = Form(models.NonFutureDateModel.Form.Mapping)

  def present = Action { implicit request =>
    Ok(views.html.valtechNonFutureDateView(form.fill()))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechNonFutureDateView(invalidForm)),
        validForm => {
          val msg = s"Success - you have correctly declared your intent to proceed"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
