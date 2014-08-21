package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.mvc.{Action, Controller}

class HealthCheck extends Controller {

  def respond = Action { request =>
    Ok("")
  }
}