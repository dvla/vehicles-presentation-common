package uk.gov.dvla.vehicles.presentation.common.testhelpers

import org.joda.time.DateTime
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSession, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.k2kandacquire.models.BusinessKeeperDetailsFormModel
import uk.gov.dvla.vehicles.presentation.common.k2kandacquire.models.BusinessKeeperDetailsFormModel._
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel._
import uk.gov.dvla.vehicles.presentation.common.model._

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
  final val VehicleLookupFailureResponseCode = "disposal_vehiclelookupfailure"
  final val RegistrationNumberValid = "AB12AWR"
  final val VehicleMakeValid = "Alfa Romeo"
  final val VehicleModelValid = "Alfasud ti"
  private val session = new ClearTextClientSideSession(TrackingIdValue)

  def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }

 private def createCookie[A](key: String, value: String): Cookie = {
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, value)
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
    disposeFlag = Some(false)
  )

  val defaultBusinessKeeperDetailsModel = BusinessKeeperDetailsFormModel(
    fleetNumber = Some(FleetNumberValid),
    businessName = BusinessNameValid,
    email = Some(EmailValid),
    postcode = PostcodeValid
  )

  def vehicleAndKeeperDetailsCookie(value: VehicleAndKeeperDetailsModel = defaultVehicleAndKeeperDetailsModel): Cookie =
    createCookie(VehicleAndKeeperLookupDetailsCacheKey, value)

  def businessKeeperDetailsCookie(value: BusinessKeeperDetailsFormModel = defaultBusinessKeeperDetailsModel) : Cookie =
    createCookie(BusinessKeeperDetailsCacheKey, value)
}
