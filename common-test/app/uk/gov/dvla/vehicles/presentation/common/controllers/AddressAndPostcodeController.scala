package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.views
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichForm
import uk.gov.dvla.vehicles.presentation.common.models.EnterAddressManuallyFormModel

class AddressAndPostcodeController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private[controllers] val form = Form(
    EnterAddressManuallyFormModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.addressAndPostcodeView(form.fill()))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.addressAndPostcodeView(invalidForm)),
        validForm => {
          val msg = s"Success - a valid model has been submitted. ${validForm.addressAndPostcodeModel}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
