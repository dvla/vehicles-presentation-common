package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import java.io.{PrintWriter, StringWriter}
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats

class HealthCheck @Inject()(healthStats: HealthStats) extends Controller {

  def respond = Action { request =>
    healthStats.healthy match {
      case Some(notHealthy) => InternalServerError(notHealthy.details)
      case _ => Ok("VMPR Application Healthy!")
    }
  }

  def debug = Action { request =>
    val debugStr = new StringWriter()
    healthStats.debug(new PrintWriter(debugStr))
    Ok(debugStr.toString)
  }
}
