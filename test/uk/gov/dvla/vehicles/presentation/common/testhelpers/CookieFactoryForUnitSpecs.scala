package uk.gov.dvla.vehicles.presentation.common.testhelpers

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{TrackingId, ClearTextClientSideSession, NoCookieFlags}
import common.controllers.VehicleLookupFormModel
import common.controllers.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import common.controllers.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
import common.mappings.TitleType
import common.model.BruteForcePreventionModel
import common.model.BruteForcePreventionModel.bruteForcePreventionViewModelCacheKey
import common.model.BusinessKeeperDetailsFormModel
import common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import common.model.CacheKeyPrefix
import common.model.NewKeeperChooseYourAddressFormModel
import common.model.NewKeeperChooseYourAddressViewModel
import common.model.PrivateKeeperDetailsFormModel
import common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import common.model.VehicleAndKeeperDetailsModel
import common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey

object CookieFactoryForUnitSpecs {
  final val FleetNumberValid = "123456"
  final val BusinessNameValid = "Brand New Motors"
  final val EmailValid = "my@email.com"
  final val PostcodeValid = "QQ99QQ"
  final val PostcodeInvalid = "XX99XX"

  implicit private val cookieFlags = new NoCookieFlags()
  final val TrackingIdValue = "trackingId"
  final val KeeperEmail = "abc@def.com"
  final val SeenCookieTrue = "yes"
  final val ConsentTrue = "true"
  final val VehicleLookupFailureResponseCode = "disposal-vehiclelookupfailure"
  final val RegistrationNumberValid = "AB12AWR"
  final val ReferenceNumberValid = "12345678910"
  final val VehicleMakeValid = "Alfa Romeo"
  final val VehicleModelValid = "Alfasud ti"
  private val session = new ClearTextClientSideSession(TrackingId(TrackingIdValue))

  final val FirstNameValid = "fn"
  final val LastNameValid = "TestLastName"
  final val DriverNumberValid = "ABCD9711215EFLGH"
  final val DayDateOfBirthValid = "24"
  final val MonthDateOfBirthValid = "12"
  final val YearDateOfBirthValid = "1920"

  def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }

  val defaultVehicleAndKeeperDetailsModel = VehicleAndKeeperDetailsModel(
    registrationNumber = RegistrationNumberValid,
    make = Some(VehicleMakeValid),
    model = Some(VehicleModelValid),
    title = None,
    firstName = None,
    lastName = None,
    address = None,
    keeperEndDate = None,
    keeperChangeDate = None,
    disposeFlag = Some(false),
    suppressedV5Flag = None
  )

  def vehicleAndKeeperDetailsCookie(value: VehicleAndKeeperDetailsModel = defaultVehicleAndKeeperDetailsModel)
                                   (implicit prefix: CacheKeyPrefix): Cookie =
    createCookie(vehicleAndKeeperLookupDetailsCacheKey, value)

  val defaultBusinessKeeperDetailsModel = BusinessKeeperDetailsFormModel(
    fleetNumber = Some(FleetNumberValid),
    businessName = BusinessNameValid,
    email = Some(EmailValid),
    postcode = PostcodeValid
  )

  def businessKeeperDetailsCookie(value: BusinessKeeperDetailsFormModel = defaultBusinessKeeperDetailsModel)
                                 (implicit prefix: CacheKeyPrefix): Cookie =
    createCookie(businessKeeperDetailsCacheKey, value)

  val defaultPrivateKeeperDetailsModel = PrivateKeeperDetailsFormModel(
    title = TitleType(1, ""),
    firstName = FirstNameValid,
    lastName = LastNameValid,
    dateOfBirth = Some(
      new LocalDate(
        YearDateOfBirthValid.toInt,
        MonthDateOfBirthValid.toInt,
        DayDateOfBirthValid.toInt
      )
    ),
    email = Some(EmailValid),
    driverNumber = Some(DriverNumberValid),
    postcode = PostcodeValid
  )

  def privateKeeperDetailsCookie(value: PrivateKeeperDetailsFormModel = defaultPrivateKeeperDetailsModel)
                                (implicit prefix: CacheKeyPrefix): Cookie =
    createCookie(privateKeeperDetailsCacheKey, value)

  val defaultBruteForcePreventionModel = BruteForcePreventionModel(
    permitted = true,
    attempts = 0,
    maxAttempts = 3,
    dateTimeISOChronology = org.joda.time.DateTime.now().toString
  )

  def bruteForcePreventionCookie(value: BruteForcePreventionModel = defaultBruteForcePreventionModel)
                                (implicit prefix: CacheKeyPrefix): Cookie =
    createCookie(bruteForcePreventionViewModelCacheKey, value)
  
  val defaultKeeperChooseYourAddressViewModel = NewKeeperChooseYourAddressViewModel(
    Form(NewKeeperChooseYourAddressFormModel.Form.Mapping).fill(NewKeeperChooseYourAddressFormModel(PostcodeValid)),
    defaultVehicleAndKeeperDetailsModel
  )

  def newKeeperChooseYourAddress(uprnSelected: String = "0"): Cookie = {
    val key = "newKeeperChooseYourAddress"
    val value = NewKeeperChooseYourAddressFormModel(uprnSelected = uprnSelected)
    createCookie(key, value)
  }

  def vehicleLookupResponseCode(responseCode: String = VehicleLookupFailureResponseCode): Cookie =
    createCookie(VehicleLookupResponseCodeCacheKey, responseCode)

  def vehicleLookupFormModel(referenceNumber: String = ReferenceNumberValid,
                             registrationNumber: String = RegistrationNumberValid): Cookie = {
    val key = VehicleLookupFormModelCacheKey
    val value = VehicleLookupFormModel(
      referenceNumber = referenceNumber,
      registrationNumber = registrationNumber
    )
    createCookie(key, value)
  }
}
