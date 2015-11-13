package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import java.text.SimpleDateFormat
import java.util.Calendar
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.views

class ResponsiveUtilitiesController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends Controller{

  val today = Calendar.getInstance().getTime()
  val minuteFormat = new SimpleDateFormat("mm")
  val hourFormat = new SimpleDateFormat("HH")
  val closingTimeOffset: Int = 15
  val closingTimeInMins: Int = hourFormat.format(today).toInt * 60 + minuteFormat.format(today).toInt + closingTimeOffset

  def present = Action { implicit request =>
    Ok(views.html.responsiveUtilitiesView(closingTimeInMins, closingTimeOffset))
  }
}
