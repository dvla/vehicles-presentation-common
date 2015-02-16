package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.Logger
import play.api.mvc.{Result, Request, Action, Controller}
import play.api.data.{Form, FormError}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.model.CacheKeyPrefix
import common.model.NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey
import common.model.PrivateKeeperDetailsFormModel
import common.model.VehicleAndKeeperDetailsModel
import common.model.PrivateKeeperDetailsFormModel.Form.DriverNumberId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailId
import common.model.PrivateKeeperDetailsFormModel.Form.LastNameId
import common.model.PrivateKeeperDetailsFormModel.Form.PostcodeId
import common.services.DateService
import common.views.helpers.FormExtensions.formBinding

abstract class PrivateKeeperDetailsBase @Inject()()
                                      (implicit protected val clientSideSessionFactory: ClientSideSessionFactory,
                                       dateService: DateService,
                                       prefix: CacheKeyPrefix
                                      ) extends Controller {

  protected def presentResult(model: VehicleAndKeeperDetailsModel, form: Form[PrivateKeeperDetailsFormModel])
                             (implicit request: Request[_]): Result

  protected def missingVehicleDetails(implicit request: Request[_]): Result

  protected def invalidFormResult(model: VehicleAndKeeperDetailsModel, form: Form[PrivateKeeperDetailsFormModel])
                                 (implicit request: Request[_]): Result

  protected def success(implicit request: Request[_]): Result

  private[controllers] val form = Form(
    PrivateKeeperDetailsFormModel.Form.detailMapping
  )

  private final val CookieErrorMessage = "Did not find VehicleDetailsModel cookie. Now redirecting to SetUpTradeDetails."

  def present = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleAndKeeperDetails) =>
        presentResult(vehicleAndKeeperDetails, form.fill())
      case _ => redirectToSetupTradeDetails(CookieErrorMessage)
    }
  }

  def submit = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleAndKeeperDetails) =>
        form.bindFromRequest.fold(
          invalidForm => invalidFormResult(
            vehicleAndKeeperDetails, formWithReplacedErrors(invalidForm)
          ),
          validForm => success
            .withCookie(validForm)
            .discardingCookie(newKeeperChooseYourAddressCacheKey))
      case _ => redirectToSetupTradeDetails(CookieErrorMessage)
    }
  }

  private def formWithReplacedErrors(form: Form[PrivateKeeperDetailsFormModel]): Form[PrivateKeeperDetailsFormModel] = {
    form.replaceError(
      LastNameId, FormError(key = LastNameId,message = "error.validLastName", args = Seq.empty)
    ).replaceError(
        DriverNumberId, FormError(key = DriverNumberId, message = "error.validDriverNumber", args = Seq.empty)
      ).replaceError(
        EmailId, FormError(key = EmailId, message = "error.email", args = Seq.empty)
      ).replaceError(
        PostcodeId, FormError(key = PostcodeId, message = "error.restricted.validPostcode", args = Seq.empty)
      ).distinctErrors
  }

  private def redirectToSetupTradeDetails(message:String)(implicit request: Request[_]) = {
    Logger.warn(message)
    missingVehicleDetails
  }
}
