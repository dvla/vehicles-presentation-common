package helpers.disposal_of_vehicle

import helpers.disposal_of_vehicle.Helper._
import mappings.disposal_of_vehicle.Dispose._
import mappings.disposal_of_vehicle.Dispose.disposeFormModelCacheKey
import mappings.disposal_of_vehicle.Dispose.disposeFormRegistrationNumberCacheKey
import mappings.disposal_of_vehicle.SetupTradeDetails.SetupTradeDetailsCacheKey
import mappings.disposal_of_vehicle.TraderDetails.traderDetailsCacheKey
import mappings.disposal_of_vehicle.VehicleLookup.vehicleLookupDetailsCacheKey
import mappings.disposal_of_vehicle.VehicleLookup.vehicleLookupFormModelCacheKey
import models.DayMonthYear
import models.domain.disposal_of_vehicle._
import play.api.libs.json.{Writes, Json}
import play.api.mvc.Cookie
import services.fakes.FakeAddressLookupService._
import services.fakes.FakeDateServiceImpl._
import services.fakes.FakeDisposeWebServiceImpl._
import services.fakes.FakeVehicleLookupWebService._
import services.fakes.{FakeDisposeWebServiceImpl, FakeVehicleLookupWebService}

object CookieFactoryForUnitSpecs {
  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val valueAsString = Json.toJson(value).toString()
    Cookie(key, valueAsString)
  }

  def setupTradeDetails(traderPostcode: String = postcodeValid) = {
    val key = SetupTradeDetailsCacheKey
    val value = SetupTradeDetailsModel(traderBusinessName = traderBusinessNameValid,
      traderPostcode = traderPostcode)
    createCookie(key, value)
  }

  def dealerDetails(uprn: Option[Long] = None, line1: String = "my house", traderPostcode: String = postcodeValid) = {
    val key = traderDetailsCacheKey
    val value = TraderDetailsModel(traderName = traderBusinessNameValid,
      traderAddress = AddressViewModel(uprn = uprn, address = Seq(line1, "my street", "my area", "my town", "CM81QJ")))
    createCookie(key, value)
  }

  def vehicleLookupFormModel(referenceNumber: String = referenceNumberValid,
                             registrationNumber: String = registrationNumberValid) = {
    val key = vehicleLookupFormModelCacheKey
    val value = VehicleLookupFormModel(referenceNumber = referenceNumber,
      registrationNumber = registrationNumber)
    createCookie(key, value)
  }

  def vehicleDetailsModel(registrationNumber: String = registrationNumberValid,
                          vehicleMake: String = FakeVehicleLookupWebService.vehicleMakeValid,
                          vehicleModel: String = vehicleModelValid,
                          keeperName: String = keeperNameValid) = {
    val key = vehicleLookupDetailsCacheKey
    val value = VehicleDetailsModel(registrationNumber = registrationNumber,
      vehicleMake = vehicleMake,
      vehicleModel = vehicleModel)
    createCookie(key, value)
  }

  def disposeFormModel() = {
    val key = disposeFormModelCacheKey
    val value = DisposeFormModel(mileage = None,
      dateOfDisposal = DayMonthYear.today,
      consent = FakeDisposeWebServiceImpl.consentValid,
      lossOfRegistrationConsent = FakeDisposeWebServiceImpl.consentValid)
    createCookie(key, value)
  }

  def disposeFormRegistrationNumber(registrationNumber: String = registrationNumberValid) =
    Cookie(disposeFormRegistrationNumberCacheKey, registrationNumber)

  def disposeFormTimestamp(timestamp: String = s"$dateOfDisposalYearValid-$dateOfDisposalMonthValid-${dateOfDisposalDayValid}T00:00:00.000+01:00") =
    Cookie(disposeFormTimestampIdCacheKey, timestamp)

  def disposeTransactionId(transactionId: String = transactionIdValid) =
    Cookie(disposeFormTransactionIdCacheKey, transactionId)

  def vehicleRegistrationNumber(registrationNumber: String = registrationNumberValid) =
    Cookie(disposeFormRegistrationNumberCacheKey, registrationNumber)

  def disposeModel(referenceNumber: String = referenceNumberValid,
                   registrationNumber: String = registrationNumberValid,
                   dateOfDisposal: DayMonthYear = DayMonthYear.today,
                   mileage: Option[Int] = None) = {
    val key = mappings.disposal_of_vehicle.Dispose.disposeModelCacheKey
    val value = DisposeModel(referenceNumber = referenceNumber,
      registrationNumber = registrationNumber,
      dateOfDisposal = dateOfDisposal,
      mileage = mileage)
    createCookie(key, value)
  }
}