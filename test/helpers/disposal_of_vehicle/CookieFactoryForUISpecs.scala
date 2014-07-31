package helpers.disposal_of_vehicle

import controllers.disposal_of_vehicle.MicroServiceError.MicroServiceErrorRefererCacheKey
import models.DayMonthYear
import models.domain.common.{AddressLinesModel, AddressAndPostcodeModel}
import viewmodels._
import BruteForcePreventionViewModel.BruteForcePreventionViewModelCacheKey
import viewmodels.BusinessChooseYourAddressViewModel
import viewmodels.BusinessChooseYourAddressViewModel.BusinessChooseYourAddressCacheKey
import viewmodels.DisposeFormViewModel
import viewmodels.DisposeFormViewModel.DisposeFormModelCacheKey
import viewmodels.DisposeFormViewModel.DisposeFormRegistrationNumberCacheKey
import viewmodels.DisposeFormViewModel.DisposeFormTimestampIdCacheKey
import viewmodels.DisposeFormViewModel.DisposeFormTransactionIdCacheKey
import viewmodels.DisposeFormViewModel.DisposeOccurredCacheKey
import viewmodels.DisposeFormViewModel.PreventGoingToDisposePageCacheKey
import DisposeViewModel.DisposeModelCacheKey
import EnterAddressManuallyViewModel.EnterAddressManuallyCacheKey
import viewmodels.SetupTradeDetailsViewModel
import viewmodels.SetupTradeDetailsViewModel.SetupTradeDetailsCacheKey
import viewmodels.TraderDetailsViewModel
import viewmodels.TraderDetailsViewModel.TraderDetailsCacheKey
import viewmodels.VehicleDetailsViewModel
import viewmodels.VehicleDetailsViewModel.VehicleLookupDetailsCacheKey
import viewmodels.VehicleLookupFormViewModel
import viewmodels.VehicleLookupFormViewModel.VehicleLookupFormModelCacheKey
import viewmodels.VehicleLookupFormViewModel.VehicleLookupResponseCodeCacheKey
import org.joda.time.DateTime
import org.openqa.selenium.{WebDriver, Cookie}
import play.api.libs.json.{Writes, Json}
import play.api.Play
import play.api.Play.current
import uk.gov.dvla.vehicles.presentation.common.controllers.AlternateLanguages.{EnId, CyId}
import webserviceclients.fakes.FakeDateServiceImpl.{DateOfDisposalYearValid, DateOfDisposalMonthValid, DateOfDisposalDayValid}
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.MaxAttempts
import webserviceclients.fakes.FakeAddressLookupService.addressWithoutUprn
import webserviceclients.fakes.FakeAddressLookupService.BuildingNameOrNumberValid
import webserviceclients.fakes.FakeAddressLookupService.Line2Valid
import webserviceclients.fakes.FakeAddressLookupService.Line3Valid
import webserviceclients.fakes.FakeAddressLookupService.PostcodeValid
import webserviceclients.fakes.FakeAddressLookupService.PostTownValid
import webserviceclients.fakes.FakeAddressLookupService.TraderBusinessNameValid
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.traderUprnValid
import webserviceclients.fakes.FakeDisposeWebServiceImpl.TransactionIdValid
import webserviceclients.fakes.FakeVehicleLookupWebService.KeeperNameValid
import webserviceclients.fakes.FakeVehicleLookupWebService.ReferenceNumberValid
import webserviceclients.fakes.FakeVehicleLookupWebService.RegistrationNumberValid
import webserviceclients.fakes.FakeVehicleLookupWebService.VehicleModelValid
import webserviceclients.fakes.{FakeDisposeWebServiceImpl, FakeVehicleLookupWebService}

object CookieFactoryForUISpecs {
  private def addCookie[A](key: String, value: A)(implicit tjs: Writes[A], webDriver: WebDriver): Unit = {
    val valueAsString = Json.toJson(value).toString()
    val manage = webDriver.manage()
    val cookie = new Cookie(key, valueAsString)
    manage.addCookie(cookie)
  }

  def withLanguageCy()(implicit webDriver: WebDriver) = {
    val key = Play.langCookieName
    val value = CyId
    addCookie(key, value)
    this
  }

  def withLanguageEn()(implicit webDriver: WebDriver) = {
    val key = Play.langCookieName
    val value = EnId
    addCookie(key, value)
    this
  }

  def setupTradeDetails(traderPostcode: String = PostcodeValid)(implicit webDriver: WebDriver) = {
    val key = SetupTradeDetailsCacheKey
    val value = SetupTradeDetailsViewModel(traderBusinessName = TraderBusinessNameValid,
      traderPostcode = traderPostcode)
    addCookie(key, value)
    this
  }

  def businessChooseYourAddress(uprn: Long = traderUprnValid)(implicit webDriver: WebDriver) = {
    val key = BusinessChooseYourAddressCacheKey
    val value = BusinessChooseYourAddressViewModel(uprnSelected = uprn.toString)
    addCookie(key, value)
    this
  }

  def enterAddressManually()(implicit webDriver: WebDriver) = {
    val key = EnterAddressManuallyCacheKey
    val value = EnterAddressManuallyViewModel(addressAndPostcodeModel = AddressAndPostcodeModel(
      addressLinesModel = AddressLinesModel(buildingNameOrNumber = BuildingNameOrNumberValid,
      line2 = Some(Line2Valid),
      line3 = Some(Line3Valid),
      postTown = PostTownValid)))
    addCookie(key, value)
    this
  }

  def dealerDetails(address: AddressViewModel = addressWithoutUprn)(implicit webDriver: WebDriver) = {
    val key = TraderDetailsCacheKey
    val value = TraderDetailsViewModel(traderName = TraderBusinessNameValid, traderAddress = address)
    addCookie(key, value)
    this
  }

  def bruteForcePreventionViewModel(permitted: Boolean = true,
                                    attempts: Int = 0,
                                    maxAttempts: Int = MaxAttempts,
                                    dateTimeISOChronology: String = org.joda.time.DateTime.now().toString)
                                   (implicit webDriver: WebDriver) = {
    val key = BruteForcePreventionViewModelCacheKey
    val value = BruteForcePreventionViewModel(
      permitted,
      attempts,
      maxAttempts,
      dateTimeISOChronology
    )
    addCookie(key, value)
    this
  }

  def vehicleLookupFormModel(referenceNumber: String = ReferenceNumberValid,
                             registrationNumber: String = RegistrationNumberValid)
                            (implicit webDriver: WebDriver) = {
    val key = VehicleLookupFormModelCacheKey
    val value = VehicleLookupFormViewModel(referenceNumber = referenceNumber,
      registrationNumber = registrationNumber)
    addCookie(key, value)
    this
  }

  def vehicleDetailsModel(registrationNumber: String = RegistrationNumberValid,
                          vehicleMake: String = FakeVehicleLookupWebService.VehicleMakeValid,
                          vehicleModel: String = VehicleModelValid,
                          keeperName: String = KeeperNameValid)
                         (implicit webDriver: WebDriver) = {
    val key = VehicleLookupDetailsCacheKey
    val value = VehicleDetailsViewModel(registrationNumber = registrationNumber,
      vehicleMake = vehicleMake,
      vehicleModel = vehicleModel)
    addCookie(key, value)
    this
  }

  def vehicleLookupResponseCode(responseCode: String = "disposal_vehiclelookupfailure")
                               (implicit webDriver: WebDriver) = {
    val key = VehicleLookupResponseCodeCacheKey
    val value = responseCode
    addCookie(key, value)
    this
  }

  def disposeFormModel()(implicit webDriver: WebDriver) = {
    val key = DisposeFormModelCacheKey
    val value = DisposeFormViewModel(mileage = None,
      dateOfDisposal = DayMonthYear.today,
      consent = FakeDisposeWebServiceImpl.ConsentValid,
      lossOfRegistrationConsent = FakeDisposeWebServiceImpl.ConsentValid)
    addCookie(key, value)
    this
  }

  def disposeModel(referenceNumber: String = ReferenceNumberValid,
                   registrationNumber: String = RegistrationNumberValid,
                   dateOfDisposal: DayMonthYear = DayMonthYear.today,
                   mileage: Option[Int] = None)(implicit webDriver: WebDriver) = {
    val key = DisposeModelCacheKey
    val value = DisposeViewModel(referenceNumber = referenceNumber,
      registrationNumber = registrationNumber,
      dateOfDisposal = dateOfDisposal,
      consent = "true",
      lossOfRegistrationConsent = "true",
      mileage = mileage)
    addCookie(key, value)
    this
  }

  def disposeTransactionId(transactionId: String = TransactionIdValid)(implicit webDriver: WebDriver) = {
    val key = DisposeFormTransactionIdCacheKey
    val value = transactionId
    addCookie(key, value)
    this
  }

  def disposeFormTimestamp()(implicit webDriver: WebDriver) = {
    val key = DisposeFormTimestampIdCacheKey
    val value = new DateTime(DateOfDisposalYearValid.toInt,
      DateOfDisposalMonthValid.toInt,
      DateOfDisposalDayValid.toInt,
      0,
      0
    ).toString()
    addCookie(key, value)
    this
  }

  def vehicleRegistrationNumber()(implicit webDriver: WebDriver) = {
    val key = DisposeFormRegistrationNumberCacheKey
    val value = RegistrationNumberValid
    addCookie(key, value)
    this
  }

  def preventGoingToDisposePage(url: String)(implicit webDriver: WebDriver) = {
    val key = PreventGoingToDisposePageCacheKey
    val value = url
    addCookie(key, value)
    this
  }

  def disposeOccurred(implicit webDriver: WebDriver) = {
    val key = DisposeOccurredCacheKey
    addCookie(key, "")
    this
  }

  def microServiceError(origin: String)(implicit webDriver: WebDriver) = {
    val key = MicroServiceErrorRefererCacheKey
    val value = origin
    addCookie(key, value)
    this
  }
}
