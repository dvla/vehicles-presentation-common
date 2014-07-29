package controllers.disposal_of_vehicle

import com.google.inject.Inject
import common.ClientSideSessionFactory
import common.CookieImplicits.{RichForm, RichSimpleResult}
import viewmodels.SetupTradeDetailsViewModel.Form.{TraderNameId, TraderPostcodeId}
import play.api.data.{Form, FormError}
import play.api.mvc.{Action, Controller}
import utils.helpers.Config
import utils.helpers.FormExtensions.formBinding
import viewmodels.SetupTradeDetailsViewModel

final class SetUpTradeDetails @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                          config: Config) extends Controller {

  private[disposal_of_vehicle] val form = Form(
    SetupTradeDetailsViewModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.disposal_of_vehicle.setup_trade_details(form.fill()))
  }

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        val formWithReplacedErrors = invalidForm.replaceError(
          TraderNameId,
          FormError(key = TraderNameId, message = "error.validTraderBusinessName", args = Seq.empty)
        ).replaceError(
          TraderPostcodeId,
          FormError(key = TraderPostcodeId, message = "error.restricted.validPostcode", args = Seq.empty)
        ).distinctErrors
        BadRequest(views.html.disposal_of_vehicle.setup_trade_details(formWithReplacedErrors))
      },
      validForm => Redirect(routes.BusinessChooseYourAddress.present()).withCookie(validForm)
    )
  }
}
