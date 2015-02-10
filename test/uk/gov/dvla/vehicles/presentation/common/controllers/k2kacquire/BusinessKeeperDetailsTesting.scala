package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import org.scalatest.mock.MockitoSugar
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.controllers.BusinessKeeperDetailsBase
import uk.gov.dvla.vehicles.presentation.common.model.BusinessKeeperDetailsViewModel

import scala.collection.mutable.ArrayBuffer


object BusinessKeeperDetailsTesting extends MockitoSugar {
  import play.api.mvc.Results.{Ok, BadRequest}

  val presentTestResult = Ok("presentResult")
  val successTestResult = Ok("successResult")
  val missingVehicleDetailsTestResult = Ok("missingVehicleDetailsResult")
  val invalidFormTestResult = BadRequest("invalidFormResult")
}

class BusinessKeeperDetailsTesting(implicit override val clientSideSessionFactory: ClientSideSessionFactory)
  extends BusinessKeeperDetailsBase {

  import BusinessKeeperDetailsTesting._

  val presentResultArgs = ArrayBuffer[BusinessKeeperDetailsViewModel]()
  val invalidFormResultArgs = ArrayBuffer[BusinessKeeperDetailsViewModel]()

  override protected def presentResult(model: BusinessKeeperDetailsViewModel)(implicit request: Request[_]): Result = {
    presentResultArgs.append(model)
    presentTestResult
  }

  override protected def success(implicit request: Request[_]): Result = successTestResult

  override protected def missingVehicleDetails(implicit request: Request[_]): Result = missingVehicleDetailsTestResult

  override protected def invalidFormResult(model: BusinessKeeperDetailsViewModel)
                                          (implicit request: Request[_]): Result = {
    invalidFormResultArgs.append(model)
    invalidFormTestResult
  }

  def clear(): Unit = {
    presentResultArgs.clear()
    invalidFormResultArgs.clear()
  }
}
