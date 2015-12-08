package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.views
import uk.gov.dvla.vehicles.presentation.common.services.DateService

class XmasOpeningTimesController @Inject()(dateService: DateService)
                                          (implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller{

  def present = Action { implicit request =>
    Ok(views.html.xmasOpeningTimesView()(dateService))
  }
}
