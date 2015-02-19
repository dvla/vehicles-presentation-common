package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import org.scalatest.mock.MockitoSugar
import play.api.data.Form
import play.api.mvc.{Request, Result}
import scala.collection.mutable.ArrayBuffer
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.controllers.PrivateKeeperDetailsBase
import uk.gov.dvla.vehicles.presentation.common.model.CacheKeyPrefix
import uk.gov.dvla.vehicles.presentation.common.model.PrivateKeeperDetailsFormModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.services.DateService

object PrivateKeeperDetailsTesting extends MockitoSugar {
  import play.api.mvc.Results.{Ok, BadRequest}

  val presentTestResult = Ok("presentResult")
  val successTestResult = Ok("successResult")
  val missingVehicleDetailsTestResult = Ok("missingVehicleDetailsResult")
  val invalidFormTestResult = BadRequest("invalidFormResult")
}

class PrivateKeeperDetailsTesting(implicit override val clientSideSessionFactory: ClientSideSessionFactory,
                                  dateService: DateService,
                                  prefix: CacheKeyPrefix) extends PrivateKeeperDetailsBase {

  import PrivateKeeperDetailsTesting._

  val presentResultArgs = ArrayBuffer[(VehicleAndKeeperDetailsModel, Form[PrivateKeeperDetailsFormModel])]()
  val invalidFormResultArgs = ArrayBuffer[(VehicleAndKeeperDetailsModel, Form[PrivateKeeperDetailsFormModel])]()

  override protected def presentResult(model: VehicleAndKeeperDetailsModel, form: Form[PrivateKeeperDetailsFormModel])
                                      (implicit request: Request[_]): Result = {
    presentResultArgs.append((model, form))
    presentTestResult
  }

  override protected def success(implicit request: Request[_]): Result = successTestResult

  override protected def missingVehicleDetails(implicit request: Request[_]): Result = missingVehicleDetailsTestResult

  override protected def invalidFormResult(model: VehicleAndKeeperDetailsModel, form: Form[PrivateKeeperDetailsFormModel])
                                          (implicit request: Request[_]): Result = {
    invalidFormResultArgs.append((model, form))
    invalidFormTestResult
  }
}
