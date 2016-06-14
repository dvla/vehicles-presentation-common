package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.{FormError, Form}
import play.api.mvc.{Result, Request, Action, Controller, AnyContent}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.model.BusinessKeeperDetailsFormModel
import common.model.CacheKeyPrefix
import common.views.models.AddressAndPostcodeViewModel
import common.views.models.AddressLinesViewModel
import common.model.NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey
import common.model.NewKeeperDetailsViewModel.createNewKeeper
import common.model.NewKeeperEnterAddressManuallyFormModel
import common.model.PrivateKeeperDetailsFormModel
import common.model.VehicleAndKeeperDetailsModel
import common.model.VmAddressModel
import common.views.helpers.FormExtensions.formBinding
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger

abstract class NewKeeperEnterAddressManuallyBase @Inject()()
               (implicit protected val clientSideSessionFactory: ClientSideSessionFactory,
                prefix: CacheKeyPrefix)
  extends Controller with DVLALogger {

  protected def presentResult(model: VehicleAndKeeperDetailsModel,
                              form: Form[NewKeeperEnterAddressManuallyFormModel])
                             (implicit request: Request[_]): Result

  protected def missingVehicleDetails(implicit request: Request[_]): Result

  protected def invalidFormResult(model: VehicleAndKeeperDetailsModel,
                                  form: Form[NewKeeperEnterAddressManuallyFormModel])
                                 (implicit request: Request[_]): Result

  protected def success(implicit request: Request[_]): Result

  val form = Form(NewKeeperEnterAddressManuallyFormModel.Form.Mapping)

  private final val KeeperDetailsNotInCacheMessage = "Failed to find keeper details in cache. " +
    "Now redirecting to vehicle lookup."
  private final val PrivateAndBusinessKeeperDetailsBothInCacheMessage = "Both private and business keeper details " +
    "found in cache. This is an error condition. Now redirecting to vehicle lookup."
  private final val VehicleDetailsNotInCacheMessage = "Failed to find vehicle details in cache. " +
    "Now redirecting to vehicle lookup"

  def present = Action { implicit request => switch(
    privateKeeperDetails => openView(privateKeeperDetails.postcode),
    businessKeeperDetails => openView(businessKeeperDetails.postcode),
    message => error(message)
  )}

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => switch(
        privateKeeperDetails =>
          handleInvalidForm(invalidForm),
        businessKeeperDetails =>
          handleInvalidForm(invalidForm),
        message => error(message)
      ),
      validForm => switch(
        privateKeeperDetails =>
          handleValidForm(validForm),
        businessKeeperDetails =>
          handleValidForm(validForm),
        message => error(message)
      )
    )
  }

  private def switch[R](onPrivate: PrivateKeeperDetailsFormModel => R,
                        onBusiness: BusinessKeeperDetailsFormModel => R,
                        onError: String => R)
                       (implicit request: Request[AnyContent]): R = {
    val privateKeeperDetailsOpt = request.cookies.getModel[PrivateKeeperDetailsFormModel]
    val businessKeeperDetailsOpt = request.cookies.getModel[BusinessKeeperDetailsFormModel]
    (privateKeeperDetailsOpt, businessKeeperDetailsOpt) match {
      case (Some(privateKeeperDetails), Some(businessKeeperDetails)) =>
        onError(PrivateAndBusinessKeeperDetailsBothInCacheMessage)
      case (Some(privateKeeperDetails), _) => onPrivate(privateKeeperDetails)
      case (_, Some(businessKeeperDetails)) => onBusiness(businessKeeperDetails)
      case _ => onError(KeeperDetailsNotInCacheMessage)
    }
  }

  private def openView(postcode: String)
                      (implicit request: Request[_]) = {
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleAndKeeperDetails) =>
        request.cookies.getModel[NewKeeperEnterAddressManuallyFormModel] match {
          case Some(formModel) =>
            presentResult(vehicleAndKeeperDetails, form.fill(formModel))
          case None =>
            presentResult(
              vehicleAndKeeperDetails,
              form.fill(
                NewKeeperEnterAddressManuallyFormModel(
                  AddressAndPostcodeViewModel(
                    AddressLinesViewModel("", None, None, ""),
                    postcode
                  )
                )
              )
            )
        }
      case _ => error(VehicleDetailsNotInCacheMessage)
    }
  }

  private def error(message: String)(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(),Warn,message)
    missingVehicleDetails
  }

  private def handleInvalidForm(invalidForm: Form[NewKeeperEnterAddressManuallyFormModel])
                               (implicit request: Request[_]) = {
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleAndKeeperDetails) =>
        invalidFormResult(vehicleAndKeeperDetails, formWithReplacedErrors(invalidForm))
      case _ => error(VehicleDetailsNotInCacheMessage)
    }
  }

  private def handleValidForm(validForm: NewKeeperEnterAddressManuallyFormModel)
                             (implicit request: Request[_]): Result = {
    createNewKeeper(VmAddressModel.from(
      validForm.addressAndPostcodeModel
    )) match {
      case Some(keeperDetails) => success
        .discardingCookie(newKeeperChooseYourAddressCacheKey)
          .withCookie(validForm)
          .withCookie(keeperDetails)
      case _ => error("No new keeper details found in cache, redirecting to vehicle lookup")
    }
  }

  def formWithReplacedErrors(form: Form[NewKeeperEnterAddressManuallyFormModel])
                                                                      : Form[NewKeeperEnterAddressManuallyFormModel] = {
    form.replaceError(
      "addressAndPostcode.addressLines.buildingNameOrNumber",
      FormError("addressAndPostcode.addressLines", "error.address.buildingNameOrNumber.invalid")
    ).replaceError(
        "addressAndPostcode.addressLines.postTown",
        FormError("addressAndPostcode.addressLines", "error.address.postTown")
      ).replaceError(
        "addressAndPostcode.postcode",
        FormError("addressAndPostcode.postcode", "error.restricted.validPostcode")
      ).distinctErrors
  }
}
