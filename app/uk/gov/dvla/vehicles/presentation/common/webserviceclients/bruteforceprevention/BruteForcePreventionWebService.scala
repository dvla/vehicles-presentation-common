package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import play.api.libs.ws.Response
import scala.concurrent.Future

// Wrapper around our webservice call so that we can IoC fake versions for testing or use the real version.
trait BruteForcePreventionWebService {
  def callBruteForce(vrm: String): Future[Response]
}