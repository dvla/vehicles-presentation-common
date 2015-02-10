package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import play.api.data.Form
import play.api.test.{FakeRequest, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{NoCookieFlags, ClearTextClientSideSessionFactory}
import uk.gov.dvla.vehicles.presentation.common.k2kandacquire.models.BusinessKeeperDetailsFormModel.Form.{BusinessNameId, EmailId, FleetNumberId, PostcodeId}
import uk.gov.dvla.vehicles.presentation.common.k2kandacquire.models.{NewKeeperChooseYourAddressFormModel, BusinessKeeperDetailsFormModel, BusinessKeeperDetailsViewModel}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieFactoryForUnitSpecs._
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieHelper
import scala.collection.mutable.ArrayBuffer
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichResult
import scala.concurrent.Await
import scala.concurrent.duration._

import scala.language.postfixOps

import scala.collection.mutable

class BusinessKeeperDetailsUnitSpec extends UnitSpec {
  final val FleetNumberValid = "123456"
  final val BusinessNameValid = "Brand New Motors"
  final val EmailValid = "my@email.com"
  final val PostcodeValid = "QQ99QQ"
  final val PostcodeInvalid = "XX99XX"

  implicit val cookieFlags = new NoCookieFlags()
  implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()

  private def controller = new BusinessKeeperDetailsTesting()

  import BusinessKeeperDetailsTesting._

  val form = Form(
    BusinessKeeperDetailsFormModel.Form.Mapping
  )

  "present" should {
    "display the page" in new WithApplication {
      val businessKeeperDetails = controller
      val request = FakeRequest().withCookies(vehicleAndKeeperDetailsCookie())

      val result = Await.result(businessKeeperDetails.present(request), 5 seconds)
      result should equal(presentTestResult)
      businessKeeperDetails.presentResultArgs should equal(ArrayBuffer(
        BusinessKeeperDetailsViewModel(
          form,
          defaultVehicleAndKeeperDetailsModel
        )
      ))
    }

    "display populated fields when cookie exists" in new WithApplication {
      val businessKeeperDetails = controller
      val request = FakeRequest().
        withCookies(vehicleAndKeeperDetailsCookie()).
        withCookies(businessKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.present(request), 5 seconds)
      result should equal(presentTestResult)
      businessKeeperDetails.presentResultArgs should equal(ArrayBuffer(
        BusinessKeeperDetailsViewModel(
          form.fill(defaultBusinessKeeperDetailsModel),
          defaultVehicleAndKeeperDetailsModel
        )
      ))
    }

    "redirect to setup trade details when no cookie is present" in new WithApplication {
      val businessKeeperDetails = controller
      val result = Await.result(businessKeeperDetails.present(FakeRequest()), 5 seconds)
      result should equal(missingVehicleDetailsTestResult)
    }
  }

  "submit" should {
    "redirect to next page when only mandatory fields are filled in" in new WithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(fleetNumber = None, email = None))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result.body should equal(successTestResult.body)
    }

    "redirect to next page when all fields are complete" in new WithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest()
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result.body should equal(successTestResult.body)
      CookieHelper.verifyCookieHasBeenDiscarded(
        NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey,
        result.cookies.values.toSeq
      )

      val defaultModel = defaultBusinessKeeperDetailsModel

      (result.cookies - NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey) should equal(Map(
        BusinessKeeperDetailsFormModel.BusinessKeeperDetailsCacheKey -> businessKeeperDetailsCookie(
          defaultModel.copy(businessName = defaultModel.businessName.toUpperCase())
        )
      ))
    }

    "redirect to setup trade details when no cookie is present with invalid submission" in new WithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(fleetNumber = Some("-12345")))
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result.body should equal(missingVehicleDetailsTestResult.body)
    }

    "return a bad request if no details are entered" in new WithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(businessName = "", postcode = ""))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      println(result.body)
      println(invalidFormTestResult.body)
      result.body should equal(invalidFormTestResult.body)
    }

    "replace required error message for business name with standard error message " in new WithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(businessName = ""))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result should equal(invalidFormTestResult)
      businessKeeperDetails.invalidFormResultArgs should have size 1
      businessKeeperDetails.invalidFormResultArgs.head.form.errors.flatMap(_.messages).mkString should equal(
        "error.validBusinessKeeperName"
      )
    }

    "replace required error message for business postcode with standard error message " in new WithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(postcode = ""))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result should equal(invalidFormTestResult)
      businessKeeperDetails.invalidFormResultArgs should have size 1
      businessKeeperDetails.invalidFormResultArgs.head.form.errors.flatMap(_.messages).mkString should equal(
        "error.restricted.validPostcode"
      )
    }
  }

  private def buildRequest(model: BusinessKeeperDetailsFormModel = defaultBusinessKeeperDetailsModel) = {
    FakeRequest().withFormUrlEncodedBody(
      FleetNumberId -> model.fleetNumber.getOrElse(""),
      BusinessNameId -> model.businessName,
      EmailId -> model.email.getOrElse(""),
      PostcodeId -> model.postcode
    )
  }
}