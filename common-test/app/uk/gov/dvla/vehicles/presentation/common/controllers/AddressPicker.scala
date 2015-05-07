package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import uk.gov.dvla.vehicles.presentation.common.{views, models}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichResult

class AddressPicker @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {
  private[controllers] val form = Form(models.AddressPickerModel.Form.Mapping)

  def present = Action { implicit request =>
    Ok(views.html.addressView(form.fill()))
  }

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => BadRequest(views.html.addressView(invalidForm, "invalid")),
      validModel => {
        Ok(views.html.success(s"Success. A valid model has been submitted. $validModel")).withCookie(validModel)
      }
    )
  }
}
