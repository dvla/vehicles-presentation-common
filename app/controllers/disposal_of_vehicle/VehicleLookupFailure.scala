package controllers.disposal_of_vehicle

import com.google.inject.Inject
import models.BruteForcePreventionModel
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{CookieImplicits, ClientSideSessionFactory}
import CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import viewmodels.{TraderDetailsViewModel, VehicleLookupFormViewModel}
import viewmodels.VehicleLookupFormViewModel.VehicleLookupResponseCodeCacheKey
import play.api.Logger
import play.api.mvc.{Action, AnyContent, Controller, DiscardingCookie, Request}
import utils.helpers.Config

final class VehicleLookupFailure @Inject()()
                                 (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends Controller {

  def present = Action { implicit request =>
    (request.cookies.getModel[TraderDetailsViewModel],
      request.cookies.getModel[BruteForcePreventionModel],
      request.cookies.getModel[VehicleLookupFormViewModel],
      request.cookies.getString(VehicleLookupResponseCodeCacheKey)) match {
      case (Some(dealerDetails),
            Some(bruteForcePreventionResponse),
            Some(vehicleLookUpFormModelDetails),
            Some(vehicleLookupResponseCode)) =>
        displayVehicleLookupFailure(
          vehicleLookUpFormModelDetails,
          bruteForcePreventionResponse,
          vehicleLookupResponseCode
        )
      case _ => Redirect(routes.SetUpTradeDetails.present())
    }
  }

  def submit = Action { implicit request =>
    (request.cookies.getModel[TraderDetailsViewModel], request.cookies.getModel[VehicleLookupFormViewModel]) match {
      case (Some(dealerDetails), Some(vehicleLookUpFormModelDetails)) =>
        Logger.debug("Found dealer and vehicle details")
        Redirect(routes.VehicleLookup.present())
      case _ => Redirect(routes.BeforeYouStart.present())
    }
  }

  private def displayVehicleLookupFailure(vehicleLookUpFormModelDetails: VehicleLookupFormViewModel,
                                          bruteForcePreventionViewModel: BruteForcePreventionModel,
                                          vehicleLookupResponseCode: String)(implicit request: Request[AnyContent]) = {
    Ok(views.html.disposal_of_vehicle.vehicle_lookup_failure(
      data = vehicleLookUpFormModelDetails,
      responseCodeVehicleLookupMSErrorMessage = vehicleLookupResponseCode)
    ).
    discardingCookies(DiscardingCookie(name = VehicleLookupResponseCodeCacheKey))
  }
}
