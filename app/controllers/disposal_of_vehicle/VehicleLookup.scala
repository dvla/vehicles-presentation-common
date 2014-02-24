package controllers.disposal_of_vehicle

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.Logger
import models.domain.disposal_of_vehicle._
import mappings.disposal_of_vehicle.VehicleLookup._
import mappings.common.V5cReferenceNumber
import V5cReferenceNumber._
import mappings.common.V5cRegistrationNumber
import V5cRegistrationNumber._
import controllers.disposal_of_vehicle.Helpers._
import play.cache.Cache
import models.domain.disposal_of_vehicle.VehicleLookupFormModel
import models.domain.disposal_of_vehicle.VehicleDetailsModel
import scala.Some
import mappings.common.Postcode._

object VehicleLookup extends Controller {

  val vehicleLookupForm = Form(
    mapping(
      v5cReferenceNumberId -> v5cReferenceNumber(minLength = 11, maxLength = 11),
      v5cRegistrationNumberId -> v5CRegistrationNumber(minLength = 2, maxLength = 8),
      v5cKeeperNameId -> nonEmptyText(minLength = 2, maxLength = 100),
      v5cPostcodeId -> postcode()
    )(VehicleLookupFormModel.apply)(VehicleLookupFormModel.unapply)
  )

  def present = Action {
    implicit request =>
      fetchDealerDetailsFromCache match {
        case Some(dealerDetails) => Ok(views.html.disposal_of_vehicle.vehicle_lookup(dealerDetails, vehicleLookupForm))
        case None => Redirect(routes.SetUpTradeDetails.present)
      }
  }

  def submit = Action {
    implicit request =>
      vehicleLookupForm.bindFromRequest.fold(
        formWithErrors => {
          fetchDealerDetailsFromCache match {
            case Some(dealerDetails) => BadRequest(views.html.disposal_of_vehicle.vehicle_lookup(dealerDetails, formWithErrors))
            case None => Redirect(routes.SetUpTradeDetails.present)
          }
        },
        f => {
          storeVehicleDetailsInCache(lookupVehicleDetails(f))
          Redirect(routes.Dispose.present)
        }
      )
  }

  private def lookupVehicleDetails(model: VehicleLookupFormModel) = {
    val knownReferenceNumber = "11111111111"
    val stubAddressAndPostcodeModel = AddressViewModel(address = Seq("1 The Avenue", "Earley", "Reading", model.v5cPostcode))
    if (model.v5cReferenceNumber == knownReferenceNumber) {
      Logger.debug(s"Selecting vehicle for ref number ${knownReferenceNumber}")
      VehicleDetailsModel(vehicleMake = "Alfa Romeo",
        vehicleModel = "Alfasud ti",
        keeperName = model.v5cKeeperName,
        keeperAddress = stubAddressAndPostcodeModel
      )
    } else {
      Logger.debug("Selecting default vehicle")
      VehicleDetailsModel(vehicleMake = "PEUGEOT",
        vehicleModel = "307 CC",
        keeperName = model.v5cKeeperName,
        keeperAddress = stubAddressAndPostcodeModel)
    }
  }

  private def storeVehicleDetailsInCache(model: VehicleDetailsModel) = {
    val key = mappings.disposal_of_vehicle.VehicleLookup.cacheKey
    Cache.set(key, model)
    Logger.debug(s"VehicleLookup page - stored vehicle details object in cache: key = $key, value = ${model}")
  }

}


