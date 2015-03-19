package uk.gov.dvla.vehicles.presentation.common.controllers

import javax.inject.Inject

import play.api.Logger
import play.api.data.{Form, FormError}
import play.api.mvc.{Action, AnyContent, Controller, Request, Result}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.model.AddressModel
import common.model.allowGoingToCompleteAndConfirmPageCacheKey
import common.model.BusinessKeeperDetailsFormModel
import common.model.CacheKeyPrefix
import common.model.NewKeeperDetailsViewModel
import common.model.NewKeeperChooseYourAddressFormModel
import common.model.NewKeeperChooseYourAddressFormModel.Form.AddressSelectId
import common.model.NewKeeperChooseYourAddressViewModel
import common.model.NewKeeperDetailsViewModel.{createNewKeeper, getTitle}
import common.model.newKeeperEnterAddressManuallyCacheKey
import common.model.PrivateKeeperDetailsFormModel
import common.model.VehicleAndKeeperDetailsModel
import common.model.VmAddressModel
import common.views.helpers.FormExtensions.formBinding
import common.webserviceclients.addresslookup.AddressLookupService


abstract class NewKeeperChooseYourAddressBase @Inject()(protected val addressLookupService: AddressLookupService)
                                          (implicit protected val clientSideSessionFactory: ClientSideSessionFactory,
                                           prefix: CacheKeyPrefix) extends Controller {
  
  protected def ordnanceSurveyUseUprn: Boolean

  protected def presentView(model: NewKeeperChooseYourAddressViewModel,
                            name: String,
                            postcode: String,
                            email: Option[String],
                            addresses: Seq[(String, String)],
                            isBusinessKeeper: Boolean,
                            fleetNumber: Option[String])(implicit request: Request[_]): Result


  protected def invalidFormResult(model: NewKeeperChooseYourAddressViewModel,
                                  name: String,
                                  postcode: String,
                                  email: Option[String],
                                  addresses: Seq[(String, String)],
                                  isBusinessKeeper: Boolean,
                                  fleetNumber: Option[String])(implicit request: Request[_]): Result
  
  protected def privateKeeperDetailsRedirect(implicit request: Request[_]): Result
  protected def businessKeeperDetailsRedirect(implicit request: Request[_]): Result
  protected def vehicleLookupRedirect(implicit request: Request[_]): Result
  protected def completeAndConfirmRedirect(implicit request: Request[_]): Result
  protected def upnpNotFoundRedirect(implicit request: Request[_]): Result
  
  
  

  val form: Form[NewKeeperChooseYourAddressFormModel] = Form(NewKeeperChooseYourAddressFormModel.Form.Mapping)

  private final val KeeperDetailsNotInCacheMessage = "Failed to find keeper details in cache. " +
    "Now redirecting to vehicle lookup."
  private final val PrivateAndBusinessKeeperDetailsBothInCacheMessage = "Both private and business keeper details " +
    "found in cache. This is an error condition. Now redirecting to vehicle lookup."
  private final val VehicleDetailsNotInCacheMessage = "Failed to find vehicle details in cache. " +
    "Now redirecting to vehicle lookup"

  private def switch[R](onPrivate: PrivateKeeperDetailsFormModel => R,
                        onBusiness: BusinessKeeperDetailsFormModel => R,
                        onError: String => R)
                       (implicit request: Request[AnyContent]): R = {
    val privateKeeperDetailsOpt = request.cookies.getModel[PrivateKeeperDetailsFormModel]
    val businessKeeperDetailsOpt = request.cookies.getModel[BusinessKeeperDetailsFormModel]
    (privateKeeperDetailsOpt, businessKeeperDetailsOpt) match {
      case (Some(privateKeeperDetails), Some(businessKeeperDetails)) => onError(PrivateAndBusinessKeeperDetailsBothInCacheMessage)
      case (Some(privateKeeperDetails), _) => onPrivate(privateKeeperDetails)
      case (_, Some(businessKeeperDetails)) => onBusiness(businessKeeperDetails)
      case _ => onError(KeeperDetailsNotInCacheMessage)
    }
  }

  def present = Action.async { implicit request => switch(
    privateKeeperDetails => fetchAddresses(privateKeeperDetails.postcode).map { addresses =>
      if (ordnanceSurveyUseUprn) openView(
        constructPrivateKeeperName(privateKeeperDetails),
        privateKeeperDetails.postcode,
        privateKeeperDetails.email,
        addresses,
        isBusinessKeeper = false,
        None
      ) else openView(
        constructPrivateKeeperName(privateKeeperDetails),
        privateKeeperDetails.postcode,
        privateKeeperDetails.email,
        index(addresses),
        isBusinessKeeper = false,
        None
      )
    },
    businessKeeperDetails => fetchAddresses(businessKeeperDetails.postcode).map { addresses =>
      if (ordnanceSurveyUseUprn) openView(
        businessKeeperDetails.businessName,
        businessKeeperDetails.postcode,
        businessKeeperDetails.email,
        addresses,
        isBusinessKeeper = true,
        businessKeeperDetails.fleetNumber
      ) else openView(
        businessKeeperDetails.businessName,
        businessKeeperDetails.postcode,
        businessKeeperDetails.email,
        index(addresses),
        isBusinessKeeper = true,
        businessKeeperDetails.fleetNumber
      )
    },
    message => Future.successful(error(message))
  )}

  def submit = Action.async { implicit request =>
    def onInvalidForm(implicit invalidForm: Form[NewKeeperChooseYourAddressFormModel]) = switch(
      privateKeeperDetails => fetchAddresses(privateKeeperDetails.postcode).map { addresses =>
        if (ordnanceSurveyUseUprn) handleInvalidForm(
          constructPrivateKeeperName(privateKeeperDetails),
          privateKeeperDetails.postcode,
          privateKeeperDetails.email,
          addresses,
          isBusinessKeeper = false,
          None
        ) else handleInvalidForm(
          constructPrivateKeeperName(privateKeeperDetails),
          privateKeeperDetails.postcode,
          privateKeeperDetails.email,
          index(addresses),
          isBusinessKeeper = false,
          None
        )
      },
      businessKeeperDetails => fetchAddresses(businessKeeperDetails.postcode).map { addresses =>
        if (ordnanceSurveyUseUprn) handleInvalidForm(
          businessKeeperDetails.businessName,
          businessKeeperDetails.postcode,
          businessKeeperDetails.email,
          addresses,
          isBusinessKeeper = true,
          businessKeeperDetails.fleetNumber
        ) else handleInvalidForm(
          businessKeeperDetails.businessName,
          businessKeeperDetails.postcode,
          businessKeeperDetails.email,
          index(addresses),
          isBusinessKeeper = true,
          businessKeeperDetails.fleetNumber
        )
      },
      message => Future.successful(error(message))
    )

    def onValidForm(implicit validModel: NewKeeperChooseYourAddressFormModel) = switch(
      privateKeeperDetails =>
        if (ordnanceSurveyUseUprn) lookupUprn(
          constructPrivateKeeperName(privateKeeperDetails),
          privateKeeperDetails.email,
          None,
          isBusinessKeeper = false
        ) else lookupAddressByPostcodeThenIndex(validModel, privateKeeperDetails.postcode),
      businessKeeperDetails =>
        if (ordnanceSurveyUseUprn) lookupUprn(
          businessKeeperDetails.businessName,
          businessKeeperDetails.email,
          businessKeeperDetails.fleetNumber,
          isBusinessKeeper = true
        ) else lookupAddressByPostcodeThenIndex(validModel, businessKeeperDetails.postcode),
      message => Future.successful(error(message))
    )

    form.bindFromRequest.fold(onInvalidForm(_), onValidForm(_))
  }

  def back = Action { implicit request =>
    switch(
      privateKeeperDetails => privateKeeperDetailsRedirect, //Redirect(routes.PrivateKeeperDetails.present()),
      businessKeeperDetails =>  businessKeeperDetailsRedirect, //Redirect(routes.BusinessKeeperDetails.present()),
      message => error(message)
    )
  }

  private def handleInvalidForm(name: String,
                                postcode: String,
                                email: Option[String],
                                addresses: Seq[(String, String)],
                                isBusinessKeeper: Boolean,
                                fleetNumber: Option[String])
                               (implicit invalidForm: Form[NewKeeperChooseYourAddressFormModel], request: Request[_]) = {
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) =>
        invalidFormResult(NewKeeperChooseYourAddressViewModel(formWithReplacedErrors(invalidForm), vehicleDetails),
          name,
          postcode,
          email,
          addresses,
          isBusinessKeeper,
          fleetNumber
        )
      case _ => error(VehicleDetailsNotInCacheMessage)
    }
  }

  private def error(message: String)(implicit request: Request[_]): Result = {
    Logger.warn(s"$message with tracking id: ${request.cookies.trackingId()} ")
    vehicleLookupRedirect
//    Redirect(routes.VehicleLookup.present())
  }

  private def formWithReplacedErrors(form: Form[NewKeeperChooseYourAddressFormModel])(implicit request: Request[_]) =
    form.replaceError(AddressSelectId, "error.required",
      FormError(
        key = AddressSelectId,
        message = "change_keeper_newKeeperChooseYourAddress.address.required", args = Seq.empty
      )
    ).distinctErrors

  private def constructPrivateKeeperName(privateKeeperDetails: PrivateKeeperDetailsFormModel): String =
    s"${getTitle(privateKeeperDetails.title)} ${privateKeeperDetails.firstName} ${privateKeeperDetails.lastName}"

  private def fetchAddresses(postcode: String)(implicit request: Request[_]) = {
    val session = clientSideSessionFactory.getSession(request.cookies)
    addressLookupService.fetchAddressesForPostcode(postcode, session.trackingId)
  }

  private def lookupUprn(newKeeperName: String,
                         email: Option[String],
                         fleetNumber: Option[String],
                         isBusinessKeeper: Boolean)
                        (implicit model: NewKeeperChooseYourAddressFormModel, request: Request[_]) = {
    val session = clientSideSessionFactory.getSession(request.cookies)
    val lookedUpAddress = addressLookupService.fetchAddressForUprn(model.uprnSelected.toString, session.trackingId)
    lookedUpAddress.map {
      case Some(addressViewModel) =>
        createNewKeeper(addressViewModel) match {
          case Some(newKeeperDetails) =>
            completeAndConfirmRedirect//Redirect(routes.CompleteAndConfirm.present())
              .discardingCookie(newKeeperEnterAddressManuallyCacheKey)
              .withCookie(model)
              .withCookie(newKeeperDetails)
              .withCookie(allowGoingToCompleteAndConfirmPageCacheKey, "true")
          case _ => error("No new keeper details found in cache, redirecting to vehicle lookup")
        }
      case None => upnpNotFoundRedirect //Redirect(routes.UprnNotFound.present())
    }
  }

  private def openView(name: String,
                       postcode: String,
                       email: Option[String],
                       addresses: Seq[(String, String)],
                       isBusinessKeeper: Boolean,
                       fleetNumber: Option[String])
                      (implicit request: Request[_]) =
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleAndKeeperDetails) =>
        presentView(NewKeeperChooseYourAddressViewModel(form.fill(), vehicleAndKeeperDetails),
          name, postcode, email, addresses, isBusinessKeeper, fleetNumber)
      case _ => error(VehicleDetailsNotInCacheMessage)
    }

  private def index(addresses: Seq[(String, String)]) =
    addresses.map {
      case (uprn, address) => address
    }. // Extract the address.
      zipWithIndex. // Add an index for each address
      map {
      case (address, index) => (index.toString, address)
    } // Flip them around so index comes first.

  private def lookupAddressByPostcodeThenIndex(model: NewKeeperChooseYourAddressFormModel,
                                               postCode: String)
                                              (implicit request: Request[_]): Future[Result] = {
    fetchAddresses(postCode)(request).map { addresses =>
      val indexSelected = model.uprnSelected.toInt

      if (indexSelected < addresses.length) {
        val lookedUpAddresses = index(addresses)
        val lookedUpAddress = lookedUpAddresses(indexSelected) match {
          case (index, address) => address
        }
        val addressModel = VmAddressModel.from(lookedUpAddress)
        createNewKeeper(addressModel) match {
          case Some(newKeeperDetails) => nextPage(model, newKeeperDetails, addressModel)
          case _ => error("No new keeper details found in cache, redirecting to vehicle lookup")
        }
      }
      else {
        // Guard against IndexOutOfBoundsException
        upnpNotFoundRedirect
//        Redirect(routes.UprnNotFound.present())
      }
    }
  }

  private def nextPage(newKeeperDetailsChooseYourAddressModel: NewKeeperChooseYourAddressFormModel,
                       newKeeperDetailsmodel: NewKeeperDetailsViewModel,
                       addressModel: AddressModel)
                      (implicit request: Request[_]): Result = {
    /* The redirect is done as the final step within the map so that:
     1) we are not blocking threads
     2) the browser does not change page before the future has completed and written to the cache. */
//    Redirect(routes.CompleteAndConfirm.present()).
    completeAndConfirmRedirect.
      discardingCookie(newKeeperEnterAddressManuallyCacheKey).
      withCookie(newKeeperDetailsmodel).
      withCookie(newKeeperDetailsChooseYourAddressModel).
      withCookie(allowGoingToCompleteAndConfirmPageCacheKey, "true")
  }
}
