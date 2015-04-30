package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import play.api.data.Form
import play.api.test.{FakeRequest}
import uk.gov.dvla.vehicles.presentation.common.{WithApplication, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieHelper
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CookieImplicits.RichResult
import common.clientsidesession.{NoCookieFlags, ClearTextClientSideSessionFactory}
import uk.gov.dvla.vehicles.presentation.common.mappings.{OptionalToggle, TitlePickerString}
import common.model.{CacheKeyPrefix, PrivateKeeperDetailsFormModel, NewKeeperChooseYourAddressFormModel}
import common.model.PrivateKeeperDetailsFormModel.Form.DateOfBirthId
import common.model.PrivateKeeperDetailsFormModel.Form.DriverNumberId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailOptionId
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import common.model.PrivateKeeperDetailsFormModel.Form.FirstNameId
import common.model.PrivateKeeperDetailsFormModel.Form.LastNameId
import common.model.PrivateKeeperDetailsFormModel.Form.PostcodeId
import common.model.PrivateKeeperDetailsFormModel.Form.TitleId
import common.services.DateServiceImpl
import common.testhelpers.CookieFactoryForUnitSpecs.defaultPrivateKeeperDetailsModel
import common.testhelpers.CookieFactoryForUnitSpecs.defaultVehicleAndKeeperDetailsModel
import common.testhelpers.CookieFactoryForUnitSpecs.privateKeeperDetailsCookie
import common.testhelpers.CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsCookie
import common.UnitSpec

class PrivateKeeperDetailsBaseUnitSpec extends UnitSpec {

  implicit val cookieFlags = new NoCookieFlags()
  implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()
  implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")
  implicit val DateService = new DateServiceImpl()

  private def controller = new PrivateKeeperDetailsTesting()

  import PrivateKeeperDetailsTesting._

  def strip[M](form: Form[M]): M =
    form.fold(
      invalidForm => throw new Exception("Error extracting model from form"),
      validModel => validModel
    )

  def verifyInvalidForm(form: Form[_]): Unit =
    form.fold(
      invalidForm => "Just return a string - this is the success case",
      validModel => throw new Exception("Error extracting model from form")
    )

  "present" should {
    "display the page" in new WithApplication {
      val privateKeeperDetails = controller
      val request = FakeRequest().withCookies(vehicleAndKeeperDetailsCookie())

      val result = Await.result(privateKeeperDetails.present(request), 5 seconds)
      result should equal(presentTestResult)

      privateKeeperDetails.presentResultArgs.map {
        case (model, form) =>
          // If an exception is thrown here then it indicates that the form is not invalid and the test fails
          // If you call fold on a form that has not been filled, Play regards it as being invalid. We are expecting
          // this form to be invalid because it is a new form that has not yet been filled or submitted
          verifyInvalidForm(form)
          model // Just return the model and verify that it is there in a collection
      } should equal(Seq(defaultVehicleAndKeeperDetailsModel))
    }

    "display populated fields when cookie exists" in new WithApplication {
      val privateKeeperDetails = controller
      val request = FakeRequest().
        withCookies(vehicleAndKeeperDetailsCookie()).
        withCookies(privateKeeperDetailsCookie())
      val result = Await.result(privateKeeperDetails.present(request), 5 seconds)
      result should equal(presentTestResult)

      privateKeeperDetails.presentResultArgs.map {
        case (model, form) => (model, strip(form))
      } should equal(ArrayBuffer((defaultVehicleAndKeeperDetailsModel, defaultPrivateKeeperDetailsModel)))
    }

    "redirect to setup trade details when no cookie is present" in new WithApplication {
      val privateKeeperDetails = controller
      val result = Await.result(privateKeeperDetails.present(FakeRequest()), 5 seconds)
      result should equal(missingVehicleDetailsTestResult)
    }
  }

  "submit" should {
    "redirect to next page when only mandatory fields are filled in" in new WithApplication {
      val privateKeeperDetails = controller
      val request = buildRequest(defaultPrivateKeeperDetailsModel.copy(dateOfBirth = None, driverNumber = None,  email = None))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(privateKeeperDetails.submit(request), 5 seconds)
      result.body should equal(successTestResult.body)
    }

    "redirect to next page when all fields are complete" in new WithApplication {
      val privateKeeperDetails = controller
      val request = buildRequest()
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(privateKeeperDetails.submit(request), 5 seconds)
      result.body.toString should equal(successTestResult.body.toString)

      CookieHelper.verifyCookieHasBeenDiscarded(
        NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey,
        result.cookies.values.toSeq
      )
    }

    "redirect to setup trade details when no cookie is present with invalid submission" in new WithApplication {
      val privateKeeperDetails = controller
      val request = buildRequest(defaultPrivateKeeperDetailsModel.copy(postcode = "12345"))
      val result = Await.result(privateKeeperDetails.submit(request), 5 seconds)
      result.body should equal(missingVehicleDetailsTestResult.body)
    }

    "return a bad request if no details are entered" in new WithApplication {
      val privateKeeperDetails = controller
      val request = buildRequest(defaultPrivateKeeperDetailsModel.copy(firstName = "", lastName = ""))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(privateKeeperDetails.submit(request), 5 seconds)
      result.body should equal(invalidFormTestResult.body)
    }

    "replace required error message for last name with standard error message " in new WithApplication {
      val privateKeeperDetails = controller
      val request = buildRequest(defaultPrivateKeeperDetailsModel.copy(lastName = ""))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(privateKeeperDetails.submit(request), 5 seconds)
      result should equal(invalidFormTestResult)
      privateKeeperDetails.invalidFormResultArgs should have size 1
      privateKeeperDetails.invalidFormResultArgs.head._2.errors.flatMap(_.messages).mkString should equal(
        "error.validLastName"
      )
    }

    "replace required error message for email with standard error message " in new WithApplication {
      val privateKeeperDetails = controller
      val request = buildRequest(defaultPrivateKeeperDetailsModel.copy(email = Some("123")))
        .withCookies(vehicleAndKeeperDetailsCookie())
      val result = Await.result(privateKeeperDetails.submit(request), 5 seconds)
      result should equal(invalidFormTestResult)
      privateKeeperDetails.invalidFormResultArgs should have size 1
      privateKeeperDetails.invalidFormResultArgs.head._2.errors.flatMap(_.messages).mkString should equal(
        "error.email"
      )
    }
  }

  private def buildRequest(model: PrivateKeeperDetailsFormModel = defaultPrivateKeeperDetailsModel) = {
    val day: String = model.dateOfBirth.map(_.getDayOfMonth.toString).getOrElse("")
    val month: String = model.dateOfBirth.map(_.getMonthOfYear.toString).getOrElse("")
    val year: String = model.dateOfBirth.map(_.getYear.toString).getOrElse("")
    FakeRequest().withFormUrlEncodedBody(Seq(
      s"$TitleId.${TitlePickerString.TitleRadioKey}" -> model.title.titleType.toString,
      FirstNameId -> model.firstName,
      LastNameId -> model.lastName,
      s"$DateOfBirthId.day" -> day,
      s"$DateOfBirthId.month" -> month,
      s"$DateOfBirthId.year" -> year,
      DriverNumberId -> model.driverNumber.getOrElse(""),
      PostcodeId -> model.postcode
    ) ++ model.email.fold(Seq(EmailOptionId -> OptionalToggle.Invisible)){ email =>
      Seq(
        EmailOptionId -> OptionalToggle.Visible,
        s"$EmailId.$EmailEnterId" -> model.email.getOrElse(""),
        s"$EmailId.$EmailVerifyId" -> model.email.getOrElse("")
      )
    }:_*)
  }
}
