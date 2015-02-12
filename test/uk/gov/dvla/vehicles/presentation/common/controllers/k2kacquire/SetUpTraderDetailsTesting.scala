package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import org.scalatest.mock.MockitoSugar
import play.api.data.Form
import play.api.mvc.{Result, Request}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.controllers.SetUpTradeDetailsBase
import uk.gov.dvla.vehicles.presentation.common.model.{SetupTradeDetailsFormModel, CacheKeyPrefix}

import scala.collection.mutable.ArrayBuffer

object SetUpTraderDetailsTesting extends MockitoSugar {
  import play.api.mvc.Results.{Ok, BadRequest}

  val presentTestResult = Ok("presentResult")
  val successTestResult = Ok("successResult")
  val invalidFormTestResult = BadRequest("invalidFormResult")
}


class SetUpTraderDetailsTesting(implicit override val clientSideSessionFactory: ClientSideSessionFactory,
                                   prefix: CacheKeyPrefix) extends SetUpTradeDetailsBase {

  import SetUpTraderDetailsTesting._
  
  
  val presentResultArgs = ArrayBuffer[SetupTradeDetailsFormModel]()
  val invalidFormResultArgs = ArrayBuffer[SetupTradeDetailsFormModel]()

  override protected def presentResult(model: Form[SetupTradeDetailsFormModel])(implicit request: Request[_]): Result = {
    presentResultArgs.append(model.get)
    presentTestResult
  }

  override protected def success(implicit request: Request[_]): Result = successTestResult
  

  override protected def invalidFormResult(model: Form[SetupTradeDetailsFormModel])
                                          (implicit request: Request[_]): Result = {
    invalidFormResultArgs.append(model.get)
    invalidFormTestResult
  }

  def clear(): Unit = {
    presentResultArgs.clear()
    invalidFormResultArgs.clear()
  }
}
