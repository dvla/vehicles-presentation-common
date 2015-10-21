package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import org.scalatest.mock.MockitoSugar
import play.api.data.Form
import play.api.mvc.{Request, Result}
import scala.collection.mutable.ArrayBuffer
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.controllers.NewKeeperEnterAddressManuallyBase
import uk.gov.dvla.vehicles.presentation.common.model.CacheKeyPrefix
import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperEnterAddressManuallyFormModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.services.DateService

object NewKeeperEnterAddressManuallyTesting extends MockitoSugar {
  import play.api.mvc.Results.{Ok, BadRequest}

  val presentTestResult = Ok("presentResult")
  val successTestResult = Ok("successResult")
  val missingVehicleDetailsTestResult = Ok("missingVehicleDetailsResult")
  val invalidFormTestResult = BadRequest("invalidFormResult")
}

class NewKeeperEnterAddressManuallyTesting(implicit override val clientSideSessionFactory: ClientSideSessionFactory,
                                  dateService: DateService,
                                  prefix: CacheKeyPrefix) extends NewKeeperEnterAddressManuallyBase {

  import NewKeeperEnterAddressManuallyTesting._

  val presentResultArgs = ArrayBuffer[(VehicleAndKeeperDetailsModel, Form[NewKeeperEnterAddressManuallyFormModel])]()
  val invalidFormResultArgs = ArrayBuffer[(VehicleAndKeeperDetailsModel, Form[NewKeeperEnterAddressManuallyFormModel])]()

  protected def presentResult(model: VehicleAndKeeperDetailsModel,
                              form: Form[NewKeeperEnterAddressManuallyFormModel])
                             (implicit request: Request[_]): Result = {
    presentResultArgs.append((model, form))
    presentTestResult
  }

  override protected def success(implicit request: Request[_]): Result = successTestResult

  override protected def missingVehicleDetails(implicit request: Request[_]): Result = missingVehicleDetailsTestResult

  protected def invalidFormResult(model: VehicleAndKeeperDetailsModel,
                                  form: Form[NewKeeperEnterAddressManuallyFormModel])
                                 (implicit request: Request[_]): Result = {
    invalidFormResultArgs.append((model, form))
    invalidFormTestResult
  }
}
