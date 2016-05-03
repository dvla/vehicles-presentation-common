package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import play.api.data.Form
import play.api.test.FakeRequest
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle
import uk.gov.dvla.vehicles.presentation.common.model.PrivateKeeperDetailsFormModel.Form._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{NoCookieFlags, ClearTextClientSideSessionFactory}
import common.clientsidesession.CookieImplicits.RichResult
import common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import common.model.BusinessKeeperDetailsFormModel
import common.model.BusinessKeeperDetailsViewModel
import common.model.BusinessKeeperDetailsFormModel.Form.BusinessNameId
import common.model.BusinessKeeperDetailsFormModel.Form.EmailId
import common.model.BusinessKeeperDetailsFormModel.Form.EmailOptionId
import common.model.BusinessKeeperDetailsFormModel.Form.FleetNumberId
import common.model.BusinessKeeperDetailsFormModel.Form.FleetNumberOptionId
import common.model.BusinessKeeperDetailsFormModel.Form.PostcodeId
import common.model.CacheKeyPrefix
import common.model.NewKeeperChooseYourAddressFormModel
import common.testhelpers.CookieFactoryForUnitSpecs.businessKeeperDetailsCookie
import common.testhelpers.CookieFactoryForUnitSpecs.defaultBusinessKeeperDetailsModel
import common.testhelpers.CookieFactoryForUnitSpecs.defaultVehicleAndKeeperDetailsModel
import common.testhelpers.CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsCookie
import common.testhelpers.CookieHelper
import uk.gov.dvla.vehicles.presentation.common.{TestWithApplication, UnitSpec}


class BusinessKeeperDetailsBaseUnitSpec extends UnitSpec {
  final val FleetNumberValid = "123456"
  final val BusinessNameValid = "Brand New Motors"
  final val EmailValid = "my@email.com"
  final val PostcodeValid = "QQ99QQ"
  final val PostcodeInvalid = "XX99XX"

  implicit val cookieFlags = new NoCookieFlags()
  implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()
  implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")

  private def controller = new BusinessKeeperDetailsTesting()

  import BusinessKeeperDetailsTesting._

  val form = Form(
    BusinessKeeperDetailsFormModel.Form.Mapping
  )

  "present" should {
    "display the page" in new TestWithApplication {
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

    "display populated fields when cookie exists" in new TestWithApplication {
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

    "redirect to setup trade details when no cookie is present" in new TestWithApplication {
      val businessKeeperDetails = controller
      val result = Await.result(businessKeeperDetails.present(FakeRequest()), 5 seconds)
      result should equal(missingVehicleDetailsTestResult)
    }
  }

  "submit" should {
    "redirect to next page when only mandatory fields are filled in" in new TestWithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(fleetNumber = None, email = None))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result.body should equal(successTestResult.body)
    }

    "redirect to next page when all fields are complete" in new TestWithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest()
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result.body should equal(successTestResult.body)
      CookieHelper.verifyCookieHasBeenDiscarded(
        NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey,
        result.cookies.values.toSeq
      )

      val defaultModel = defaultBusinessKeeperDetailsModel

      (result.cookies - NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey) should equal(Map(
        BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey -> businessKeeperDetailsCookie(
          defaultModel.copy(businessName = defaultModel.businessName.toUpperCase)
        )
      ))
    }

    "redirect to setup trade details when no cookie is present with invalid submission" in new TestWithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(fleetNumber = Some("-12345")))
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      result.body should equal(missingVehicleDetailsTestResult.body)
    }

    "return a bad request if no details are entered" in new TestWithApplication {
      val businessKeeperDetails = controller
      val request = buildRequest(defaultBusinessKeeperDetailsModel.copy(businessName = "", postcode = ""))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(businessKeeperDetails.submit(request), 5 seconds)
      println(result.body)
      println(invalidFormTestResult.body)
      result.body should equal(invalidFormTestResult.body)
    }

    "replace required error message for business name with standard error message " in new TestWithApplication {
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

    "replace required error message for business postcode with standard error message " in new TestWithApplication {
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
    val params = Seq(
      BusinessNameId -> model.businessName,
      PostcodeId -> model.postcode
    ) ++ model.email.fold(Seq(EmailOptionId -> OptionalToggle.Invisible)){ email =>
      Seq(
        EmailOptionId -> OptionalToggle.Visible,
        s"$EmailId.$EmailEnterId" -> model.email.getOrElse(""),
        s"$EmailId.$EmailVerifyId" -> model.email.getOrElse("")
      )
    } ++ model.fleetNumber.fold(Seq(FleetNumberOptionId -> OptionalToggle.Invisible)){ fleetNumber =>
      Seq(FleetNumberOptionId -> OptionalToggle.Visible, FleetNumberId -> fleetNumber )
    }
    FakeRequest().withFormUrlEncodedBody(params:_*)
  }
}
