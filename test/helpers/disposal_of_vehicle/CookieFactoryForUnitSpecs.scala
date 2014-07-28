package helpers.disposal_of_vehicle

import common.{ClientSideSessionFactory, CookieFlags, ClearTextClientSideSession}
import composition.TestComposition
import controllers.disposal_of_vehicle.MicroServiceError.MicroServiceErrorRefererCacheKey
import mappings.common.Help.HelpCacheKey
import mappings.disposal_of_vehicle.Dispose.SurveyRequestTriggerDateCacheKey
import mappings.disposal_of_vehicle.RelatedCacheKeys.SeenCookieMessageKey
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
import pages.disposal_of_vehicle.{VehicleLookupPage, HelpPage}
import play.api.libs.json.{Writes, Json}
import play.api.mvc.Cookie
import serviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.MaxAttempts
import serviceclients.fakes.FakeAddressLookupService.BuildingNameOrNumberValid
import serviceclients.fakes.FakeAddressLookupService.Line2Valid
import serviceclients.fakes.FakeAddressLookupService.Line3Valid
import serviceclients.fakes.FakeAddressLookupService.PostcodeValid
import serviceclients.fakes.FakeAddressLookupService.PostTownValid
import serviceclients.fakes.FakeAddressLookupService.TraderBusinessNameValid
import serviceclients.fakes.FakeAddressLookupWebServiceImpl.traderUprnValid
import serviceclients.fakes.FakeDateServiceImpl.{DateOfDisposalDayValid, DateOfDisposalMonthValid, DateOfDisposalYearValid}
import serviceclients.fakes.FakeDisposeWebServiceImpl.TransactionIdValid
import serviceclients.fakes.FakeVehicleLookupWebService.KeeperNameValid
import serviceclients.fakes.FakeVehicleLookupWebService.ReferenceNumberValid
import serviceclients.fakes.FakeVehicleLookupWebService.RegistrationNumberValid
import serviceclients.fakes.FakeVehicleLookupWebService.VehicleModelValid
import serviceclients.fakes.{FakeDateServiceImpl, FakeDisposeWebServiceImpl, FakeVehicleLookupWebService}

object CookieFactoryForUnitSpecs extends TestComposition { // TODO can we make this more fluent by returning "this" at the end of the defs

  implicit private val cookieFlags = injector.getInstance(classOf[CookieFlags])
  final val TrackingIdValue = "trackingId"
  private val session = new ClearTextClientSideSession(TrackingIdValue)

  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }

  private def createCookie[A](key: String, value: String): Cookie = {
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, value)
  }

  def seenCookieMessage(): Cookie = {
    val key = SeenCookieMessageKey
    val value = "yes" // TODO make a constant
    createCookie(key, value)
  }

  def setupTradeDetails(traderPostcode: String = PostcodeValid): Cookie = {
    val key = SetupTradeDetailsCacheKey
    val value = SetupTradeDetailsViewModel(traderBusinessName = TraderBusinessNameValid,
      traderPostcode = traderPostcode)
    createCookie(key, value)
  }

  def businessChooseYourAddress(): Cookie = {
    val key = BusinessChooseYourAddressCacheKey
    val value = BusinessChooseYourAddressViewModel(uprnSelected = traderUprnValid.toString)
    createCookie(key, value)
  }

  def enterAddressManually(): Cookie = {
    val key = EnterAddressManuallyCacheKey
    val value = EnterAddressManuallyViewModel(
      addressAndPostcodeModel = AddressAndPostcodeModel(
        addressLinesModel = AddressLinesModel(
          buildingNameOrNumber = BuildingNameOrNumberValid,
            line2 = Some(Line2Valid),
            line3 = Some(Line3Valid),
            postTown = PostTownValid
        )
      )
    )
    createCookie(key, value)
  }

  def traderDetailsModel(uprn: Option[Long] = None,
                         buildingNameOrNumber: String = BuildingNameOrNumberValid,
                         line2: String = Line2Valid,
                         line3: String = Line3Valid,
                         postTown: String = PostTownValid,
                         traderPostcode: String = PostcodeValid): Cookie = {
    val key = TraderDetailsCacheKey
    val value = TraderDetailsViewModel(
      traderName = TraderBusinessNameValid,
      traderAddress = AddressViewModel(
        uprn = uprn,
        address = Seq(buildingNameOrNumber, line2, line3, postTown, traderPostcode)
      )
    )
    createCookie(key, value)
  }

  def traderDetailsModelBuildingNameOrNumber(uprn: Option[Long] = None,
                                             buildingNameOrNumber: String = BuildingNameOrNumberValid,
                                             postTown: String = PostTownValid,
                                             traderPostcode: String = PostcodeValid): Cookie = {
    val key = TraderDetailsCacheKey
    val value = TraderDetailsViewModel(
      traderName = TraderBusinessNameValid,
      traderAddress = AddressViewModel(
        uprn = uprn,
        address = Seq(buildingNameOrNumber, postTown, traderPostcode)
      )
    )
    createCookie(key, value)
  }

  def traderDetailsModelLine2(uprn: Option[Long] = None,
                              buildingNameOrNumber: String = BuildingNameOrNumberValid,
                              line2: String = Line2Valid,
                              postTown: String = PostTownValid,
                              traderPostcode: String = PostcodeValid): Cookie = {
    val key = TraderDetailsCacheKey
    val value = TraderDetailsViewModel(
      traderName = TraderBusinessNameValid,
      traderAddress = AddressViewModel(
        uprn = uprn,
        address = Seq(buildingNameOrNumber, line2, postTown, traderPostcode)
      )
    )
    createCookie(key, value)
  }

  def traderDetailsModelPostTown(uprn: Option[Long] = None,
                                 postTown: String = PostTownValid,
                                 traderPostcode: String = PostcodeValid): Cookie = {
    val key = TraderDetailsCacheKey
    val value = TraderDetailsViewModel(
      traderName = TraderBusinessNameValid,
      traderAddress = AddressViewModel(uprn = uprn, address = Seq(postTown, traderPostcode)
      )
    )
    createCookie(key, value)
  }

  def bruteForcePreventionViewModel(permitted: Boolean = true,
                                    attempts: Int = 0,
                                    maxAttempts: Int = MaxAttempts,
                                    dateTimeISOChronology: String = org.joda.time.DateTime.now().toString): Cookie = {
    val key = BruteForcePreventionViewModelCacheKey
    val value = BruteForcePreventionViewModel(
      permitted,
      attempts,
      maxAttempts,
      dateTimeISOChronology = dateTimeISOChronology
    )
    createCookie(key, value)
  }

  def vehicleLookupFormModel(referenceNumber: String = ReferenceNumberValid,
                             registrationNumber: String = RegistrationNumberValid): Cookie = {
    val key = VehicleLookupFormModelCacheKey
    val value = VehicleLookupFormViewModel(
      referenceNumber = referenceNumber,
      registrationNumber = registrationNumber
    )
    createCookie(key, value)
  }

  def vehicleDetailsModel(registrationNumber: String = RegistrationNumberValid,
                          vehicleMake: String = FakeVehicleLookupWebService.VehicleMakeValid,
                          vehicleModel: String = VehicleModelValid,
                          keeperName: String = KeeperNameValid): Cookie = {
    val key = VehicleLookupDetailsCacheKey
    val value = VehicleDetailsViewModel(
      registrationNumber = registrationNumber,
      vehicleMake = vehicleMake,
      vehicleModel = vehicleModel
    )
    createCookie(key, value)
  }

  def vehicleLookupResponseCode(responseCode: String = "disposal_vehiclelookupfailure"): Cookie =
    createCookie(VehicleLookupResponseCodeCacheKey, responseCode)

  def disposeFormModel(mileage: Option[Int] = None): Cookie = {
    val key = DisposeFormModelCacheKey
    val value = DisposeFormViewModel(
      mileage = mileage,
      dateOfDisposal = DayMonthYear(
        FakeDateServiceImpl.DateOfDisposalDayValid.toInt,
        FakeDateServiceImpl.DateOfDisposalMonthValid.toInt, FakeDateServiceImpl.DateOfDisposalYearValid.toInt
      ),
      consent = FakeDisposeWebServiceImpl.ConsentValid,
      lossOfRegistrationConsent = FakeDisposeWebServiceImpl.ConsentValid
    )
    createCookie(key, value)
  }

  def trackingIdModel(value: String = TrackingIdValue): Cookie = {
    createCookie(ClientSideSessionFactory.TrackingIdCookieName, value)
  }

  def disposeFormRegistrationNumber(registrationNumber: String = RegistrationNumberValid): Cookie =
    createCookie(DisposeFormRegistrationNumberCacheKey, registrationNumber)

  private val defaultDisposeTimestamp =
    new DateTime(DateOfDisposalYearValid.toInt,
      DateOfDisposalMonthValid.toInt,
      DateOfDisposalDayValid.toInt,
      0,
      0
    ).toString()
  def disposeFormTimestamp(timestamp: String = defaultDisposeTimestamp): Cookie =
    createCookie(DisposeFormTimestampIdCacheKey, timestamp)

  def disposeTransactionId(transactionId: String = TransactionIdValid): Cookie =
    createCookie(DisposeFormTransactionIdCacheKey, transactionId)

  def vehicleRegistrationNumber(registrationNumber: String = RegistrationNumberValid): Cookie =
    createCookie(DisposeFormRegistrationNumberCacheKey, registrationNumber)

  def disposeModel(referenceNumber: String = ReferenceNumberValid,
                   registrationNumber: String = RegistrationNumberValid,
                   dateOfDisposal: DayMonthYear = DayMonthYear.today,
                   mileage: Option[Int] = None): Cookie = {
    val key = DisposeModelCacheKey
    val value = DisposeViewModel(
      referenceNumber = referenceNumber,
      registrationNumber = registrationNumber,
      dateOfDisposal = dateOfDisposal,
      consent = "true",
      lossOfRegistrationConsent = "true",
      mileage = mileage
    )
    createCookie(key, value)
  }

  def preventGoingToDisposePage(payload: String = ""): Cookie =
    createCookie(PreventGoingToDisposePageCacheKey, payload)

  def disposeOccurred = createCookie(DisposeOccurredCacheKey, "")

  def help(origin: String = HelpPage.address): Cookie = {
    val key = HelpCacheKey
    val value = origin
    createCookie(key, value)
  }

  def disposeSurveyUrl(surveyUrl: String): Cookie =
    createCookie(SurveyRequestTriggerDateCacheKey, surveyUrl)

  def microServiceError(origin: String = VehicleLookupPage.address): Cookie = {
    val key = MicroServiceErrorRefererCacheKey
    val value = origin
    createCookie(key, value)
  }
}
