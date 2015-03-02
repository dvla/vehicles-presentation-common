package uk.gov.dvla.vehicles.presentation.common.controllers

import org.scalatest.mock.MockitoSugar
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.CacheKeyPrefix

object VehicleLookupFailureTesting extends MockitoSugar {
  import play.api.mvc.Results.Ok

  val presentTestResult = Ok("presentResult")
  val missingPresentCookieDataTestResult = Ok("missingPresentCookieResult")
  val foundSubmitCookieDataTestResult = Ok("submitResult")
  val missingSubmitCookieDataTestResult = Ok("missingSubmitCookieResult")
  val vehicleLookupResponseCodeCacheKey = VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
}

case class VehicleLookupFormModel(referenceNumber: String,
                                  registrationNumber: String) extends VehicleLookupFormModelBase

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

object VehicleLookupFormModel {
  implicit val JsonFormat = Json.format[VehicleLookupFormModel]
  final val VehicleLookupFormModelCacheKey = "test-vehicleLookupFormModel"
  implicit val Key = CacheKey[VehicleLookupFormModel](VehicleLookupFormModelCacheKey)
  final val VehicleLookupResponseCodeCacheKey = "test-vehicleLookupResponseCode"
}

class VehicleLookupFailureTesting(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  prefix: CacheKeyPrefix) extends VehicleLookupFailureBase[VehicleLookupFormModel] {

  import VehicleLookupFailureTesting._

  protected override def presentResult(model: VehicleLookupFormModel, responseCode: String)
                                      (implicit request: Request[_]): Result =
    presentTestResult

  protected override def missingPresentCookieDataResult()(implicit request: Request[_]): Result =
    missingPresentCookieDataTestResult

  protected override def foundSubmitCookieDataResult()(implicit request: Request[_]): Result =
    foundSubmitCookieDataTestResult

  protected override def missingSubmitCookieDataResult()(implicit request: Request[_]): Result =
    missingSubmitCookieDataTestResult

  protected override val vehicleLookupResponseCodeCacheKey: String =
    VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
}
