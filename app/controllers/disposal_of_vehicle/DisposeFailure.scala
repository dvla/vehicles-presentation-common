package controllers.disposal_of_vehicle

import com.google.inject.Inject
import common.ClientSideSessionFactory
import common.CookieImplicits.RichCookies
import models.domain.disposal_of_vehicle.DisposeModel
import viewmodels.{DisposeFormViewModel, TraderDetailsViewModel, VehicleDetailsViewModel}
import viewmodels.DisposeFormViewModel.DisposeFormTransactionIdCacheKey
import play.api.Logger
import play.api.mvc.{Action, Controller}
import utils.helpers.Config

final class DisposeFailure @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  def present = Action { implicit request =>
    (request.cookies.getModel[TraderDetailsViewModel],
     request.cookies.getModel[DisposeFormViewModel],
     request.cookies.getModel[VehicleDetailsViewModel],
     request.cookies.getString(DisposeFormTransactionIdCacheKey)) match {
      case (Some(dealerDetails), Some(disposeFormModel), Some(vehicleDetails), Some(transactionId)) =>
        val disposeViewModel = createViewModel(dealerDetails, vehicleDetails, Some(transactionId))
        Ok(views.html.disposal_of_vehicle.dispose_failure(disposeViewModel, disposeFormModel))
      case _ =>
        Logger.debug("Could not find all expected data in cache on dispose failure present, redirecting")
        Redirect(routes.SetUpTradeDetails.present())
    }
  }

  private def createViewModel(traderDetails: TraderDetailsViewModel,
                              vehicleDetails: VehicleDetailsViewModel,
                              transactionId: Option[String]): DisposeModel =
    DisposeModel(
      registrationNumber = vehicleDetails.registrationNumber,
      vehicleMake = vehicleDetails.vehicleMake,
      vehicleModel = vehicleDetails.vehicleModel,
      dealerName = traderDetails.traderName,
      dealerAddress = traderDetails.traderAddress,
      transactionId = transactionId
    )
}
