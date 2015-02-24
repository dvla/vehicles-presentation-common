package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import org.scalatest.mock.MockitoSugar
import play.api.mvc.{Result, Request}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.controllers.{NewKeeperChooseYourAddressBase, BusinessKeeperDetailsBase}
import uk.gov.dvla.vehicles.presentation.common.model.{NewKeeperChooseYourAddressViewModel, BusinessKeeperDetailsViewModel, CacheKeyPrefix}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService

import scala.collection.mutable.ArrayBuffer

object NewKeeperChooseYourAddressBaseTesting extends MockitoSugar {
  import play.api.mvc.Results.{Ok, BadRequest}

  val presentTestResult               = Ok("presentResult")
  val successTestResult               = Ok("successResult")
  val privateKeeperDetailsTestResult  = Ok("privateKeeperDetailsTestResult")
  val businessKeeperDetailsTestResult = Ok("businessKeeperDetailsTestResult")
  val vehicleLookupTestResult         = Ok("vehicleLookupTestResult")
  val completeAndConfirmTestResult    = Ok("completeAndConfirmTestResult")
  val upnpNotFoundTestResult          = Ok("upnpNotFoundTestResult")
  val invalidFormTestResult           = BadRequest("invalidFormResult")
}

class NewKeeperChooseYourAddressBaseTesting(protected override val addressLookupService: AddressLookupService)
                                           (implicit override val clientSideSessionFactory: ClientSideSessionFactory,
                                   prefix: CacheKeyPrefix) extends NewKeeperChooseYourAddressBase(addressLookupService) {

  import NewKeeperChooseYourAddressBaseTesting._

  protected def ordnanceSurveyUseUprn: Boolean = true

  val presentResultArgs = ArrayBuffer[NewKeeperChooseYourAddressViewModel]()
  
  val invalidFormResultArgs = ArrayBuffer[NewKeeperChooseYourAddressViewModel]()

  protected def presentView(model: NewKeeperChooseYourAddressViewModel,
                            name: String,
                            postcode: String,
                            email: Option[String],
                            addresses: Seq[(String, String)],
                            isBusinessKeeper: Boolean,
                            fleetNumber: Option[String])(implicit request: Request[_]): Result = {
    presentResultArgs.append(model)
    presentTestResult
  }

  protected def invalidFormResult(model: NewKeeperChooseYourAddressViewModel,
                                  name: String,
                                  postcode: String,
                                  email: Option[String],
                                  addresses: Seq[(String, String)],
                                  isBusinessKeeper: Boolean,
                                  fleetNumber: Option[String])(implicit request: Request[_]): Result = {

    invalidFormResultArgs.append(model)
    invalidFormTestResult
  }

  protected def privateKeeperDetailsRedirect(implicit request: Request[_]): Result  = privateKeeperDetailsTestResult
  protected def businessKeeperDetailsRedirect(implicit request: Request[_]): Result = businessKeeperDetailsTestResult
  protected def vehicleLookupRedirect(implicit request: Request[_]): Result         = vehicleLookupTestResult
  protected def completeAndConfirmRedirect(implicit request: Request[_]): Result    = completeAndConfirmTestResult
  protected def upnpNotFoundRedirect(implicit request: Request[_]): Result          = upnpNotFoundTestResult
  

}

