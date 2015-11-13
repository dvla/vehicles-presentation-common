package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.Action
import play.api.mvc.Controller
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.views

class VehicleDetailPlaybackController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private val showLanguageSwitch: Boolean = true

  private val vehicleDetails = new VehicleAndKeeperDetailsModel(
      registrationNumber = "A 1",
      make = Some("Ford"),
      model = Some("Focus"),
      title = None,
      firstName = None,
      lastName = None,
      address = None,
      disposeFlag = None,
      keeperEndDate = None,
      keeperChangeDate = None,
      suppressedV5Flag = None
    )

  def present = Action { implicit request =>
    Ok(views.html.vehicleDetailPlaybackView(vehicleDetails, showLanguageSwitch))
  }
}
