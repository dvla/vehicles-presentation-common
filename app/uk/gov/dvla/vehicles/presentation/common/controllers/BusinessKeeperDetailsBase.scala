package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.{Form, FormError}
import play.api.mvc.{Action, Controller, Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.LogFormats.DVLALogger
import common.model.BusinessKeeperDetailsFormModel
import common.model.BusinessKeeperDetailsViewModel
import common.model.CacheKeyPrefix
import common.model.NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey
import common.model.VehicleAndKeeperDetailsModel
import common.views.helpers.FormExtensions.formBinding
import common.views.constraints.BusinessKeeperName

abstract class BusinessKeeperDetailsBase @Inject()()
                    (implicit protected val clientSideSessionFactory: ClientSideSessionFactory,
                     prefix: CacheKeyPrefix) extends Controller with DVLALogger {

  protected def presentResult(model: BusinessKeeperDetailsViewModel)(implicit request: Request[_]): Result

  protected def invalidFormResult(model:BusinessKeeperDetailsViewModel)(implicit request: Request[_]): Result

  protected def missingVehicleDetails(implicit request: Request[_]): Result

  protected def success(implicit request: Request[_]): Result

  val form = Form(
    BusinessKeeperDetailsFormModel.Form.Mapping
  )

  private final val CookieErrorMessage = "Did not find VehicleDetailsModel cookie. Now redirecting to SetUpTradeDetails."

  def present = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleAndKeeperDetails) =>
        presentResult(BusinessKeeperDetailsViewModel(form.fill(), vehicleAndKeeperDetails))
      case _ => redirectToSetupTradeDetails(CookieErrorMessage)
    }
  }

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
          case Some(vehicleAndKeeperDetails) =>
            invalidFormResult(
              BusinessKeeperDetailsViewModel(formWithReplacedErrors(invalidForm), vehicleAndKeeperDetails)
            )
          case None => redirectToSetupTradeDetails(CookieErrorMessage)
        }
      },
      validFormModel => success
       .withCookie(validFormModel)
       .discardingCookie(newKeeperChooseYourAddressCacheKey)
    )
  }

  private def formWithReplacedErrors(form: Form[BusinessKeeperDetailsFormModel]) = {
    form.replaceError(
      FormError(key = BusinessKeeperDetailsFormModel.Form.BusinessNameId, message = BusinessKeeperName.GeneralError),
      e => e.key == BusinessKeeperDetailsFormModel.Form.BusinessNameId &&
        e.message != BusinessKeeperName.BracesOrQuotesError
    ).replaceError(
      BusinessKeeperDetailsFormModel.Form.PostcodeId,
      FormError(key = BusinessKeeperDetailsFormModel.Form.PostcodeId,message = "error.restricted.validPostcode")
    ).distinctErrors
  }

  private def redirectToSetupTradeDetails(message:String)(implicit request: Request[_]) = {
    logMessage(request.cookies.trackingId(), Warn, message)
    missingVehicleDetails
  }
}
