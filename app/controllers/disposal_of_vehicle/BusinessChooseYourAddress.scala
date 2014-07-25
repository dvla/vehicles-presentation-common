package controllers.disposal_of_vehicle

import common.CookieImplicits.{RichCookies, RichForm, RichSimpleResult}
import common.{ClientSideSession, ClientSideSessionFactory}
import javax.inject.Inject
import mappings.common.DropDown.addressDropDown
import mappings.disposal_of_vehicle.BusinessChooseYourAddress.AddressSelectId
import viewmodels.EnterAddressManuallyViewModel
import EnterAddressManuallyViewModel.EnterAddressManuallyCacheKey
import models.domain.disposal_of_vehicle.{BusinessChooseYourAddressModel, SetupTradeDetailsModel, TraderDetailsModel}
import play.api.data.Forms.mapping
import play.api.data.{Form, FormError}
import play.api.i18n.Lang
import play.api.Logger
import play.api.mvc.{Action, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import services.address_lookup.AddressLookupService
import utils.helpers.Config
import utils.helpers.FormExtensions.formBinding
import views.html.disposal_of_vehicle.business_choose_your_address

final class BusinessChooseYourAddress @Inject()(addressLookupService: AddressLookupService)
                                               (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                                config: Config) extends Controller {

  private[disposal_of_vehicle] val form = Form(
    mapping(
      /* We cannot apply constraints to this drop down as it is populated by web call to an address lookup service.
      We would need the request here to get the cookie.
      Validation is done when we make a second web call with the UPRN,
      so if a bad guy is injecting a non-existent UPRN then it will fail at that step instead */
      AddressSelectId -> addressDropDown
    )(BusinessChooseYourAddressModel.apply)(BusinessChooseYourAddressModel.unapply)
  )

  def present = Action.async { implicit request =>
    request.cookies.getModel[SetupTradeDetailsModel] match {
      case Some(setupTradeDetailsModel) =>
        val session = clientSideSessionFactory.getSession(request.cookies)
        fetchAddresses(setupTradeDetailsModel)(session, lang).map { addresses =>
          Ok(views.html.disposal_of_vehicle.business_choose_your_address(form.fill(),
            setupTradeDetailsModel.traderBusinessName,
            setupTradeDetailsModel.traderPostcode,
            addresses))
        }
      case None => Future {
        Redirect(routes.SetUpTradeDetails.present())
      }
    }
  }

  def submit = Action.async { implicit request =>
    form.bindFromRequest.fold(
      invalidForm =>
        request.cookies.getModel[SetupTradeDetailsModel] match {
          case Some(setupTradeDetails) =>
            implicit val session = clientSideSessionFactory.getSession(request.cookies)
            fetchAddresses(setupTradeDetails).map { addresses =>
              BadRequest(business_choose_your_address(formWithReplacedErrors(invalidForm),
                setupTradeDetails.traderBusinessName,
                setupTradeDetails.traderPostcode,
                addresses))
            }
          case None => Future {
            Logger.error("Failed to find dealer details, redirecting")
            Redirect(routes.SetUpTradeDetails.present())
          }
        },
      validForm =>
        request.cookies.getModel[SetupTradeDetailsModel] match {
          case Some(setupTradeDetailsModel) =>
            implicit val session = clientSideSessionFactory.getSession(request.cookies)
            lookupUprn(validForm, setupTradeDetailsModel.traderBusinessName)
          case None => Future {
            Logger.error("Failed to find dealer details, redirecting")
            Redirect(routes.SetUpTradeDetails.present())
          }
        }
    )
  }

  private def formWithReplacedErrors(form: Form[BusinessChooseYourAddressModel])(implicit request: Request[_]) =
    form.replaceError(AddressSelectId, "error.required",
      FormError(key = AddressSelectId, message = "disposal_businessChooseYourAddress.address.required", args = Seq.empty)).
      distinctErrors

  private def fetchAddresses(model: SetupTradeDetailsModel)(implicit session: ClientSideSession, lang: Lang) =
    addressLookupService.fetchAddressesForPostcode(model.traderPostcode, session.trackingId)

  private def lookupUprn(model: BusinessChooseYourAddressModel, traderName: String)
                        (implicit request: Request[_], session: ClientSideSession) = {
    val lookedUpAddress = addressLookupService.fetchAddressForUprn(model.uprnSelected.toString, session.trackingId)
    lookedUpAddress.map {
      case Some(addressViewModel) =>
        val traderDetailsModel = TraderDetailsModel(traderName = traderName, traderAddress = addressViewModel)
        /* The redirect is done as the final step within the map so that:
         1) we are not blocking threads
         2) the browser does not change page before the future has completed and written to the cache. */
        Redirect(routes.VehicleLookup.present()).
          discardingCookie(EnterAddressManuallyCacheKey).
          withCookie(model).
          withCookie(traderDetailsModel)
      case None => Redirect(routes.UprnNotFound.present())
    }
  }
}
