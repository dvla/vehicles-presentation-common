package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.mappings.{OptionalToggle, BusinessName}
import common.model.CacheKeyPrefix
import common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import common.model.SetupTradeDetailsFormModel.Form.TraderEmailId
import common.model.SetupTradeDetailsFormModel.Form.TraderEmailOptionId
import common.model.SetupTradeDetailsFormModel.Form.TraderNameId
import common.model.SetupTradeDetailsFormModel.Form.TraderPostcodeId
import common.{UnitSpec, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.model.BusinessKeeperDetailsFormModel.Form._

class SetupTradeDetailsFormSpec extends UnitSpec {

  implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")

  final val TraderBusinessNameValid = "example trader name"
  final val PostcodeWithoutAddresses = "xx99xx"
  final val PostcodeValid = "QQ99QQ"
  final val TraderEmailValid = "example@example.co.uk"

  "form" should {
    "accept if form is completed with all fields correctly" in new WithApplication {
      val model = formWithValidDefaults(
        traderBusinessName = TraderBusinessNameValid,
        traderPostcode = PostcodeValid,
        traderEmail = Some(TraderEmailValid)).get
      model.traderBusinessName should equal(TraderBusinessNameValid.toUpperCase)
      model.traderPostcode should equal(PostcodeValid)
      model.traderEmail should equal(Some(TraderEmailValid))
    }

    "accept if form is completed with mandatory fields only" in new WithApplication {
      val model = formWithValidDefaults(traderBusinessName = TraderBusinessNameValid, traderPostcode = PostcodeValid).get
      model.traderBusinessName should equal(TraderBusinessNameValid.toUpperCase)
      model.traderPostcode should equal(PostcodeValid)
    }

    "reject if form has no fields completed" in new WithApplication {
      formWithValidDefaults(traderBusinessName = "", traderPostcode = "").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required", "error.validBusinessName", "error.minLength", "error.required", "error.restricted.validPostcode")
    }
  }

  "traderBusinessName" should {
    "reject if trader business name is blank" in new WithApplication {
      // IMPORTANT: The messages being returned by the form validation are overridden by the Controller
      val errors = formWithValidDefaults(traderBusinessName = "").errors
      errors should have length 3
      errors(0).key should equal(TraderNameId)
      errors(0).message should equal("error.minLength")
      errors(1).key should equal(TraderNameId)
      errors(1).message should equal("error.required")
      errors(2).key should equal(TraderNameId)
      errors(2).message should equal("error.validBusinessName")
    }

    "reject if trader business name is less than minimum length" in new WithApplication {
      formWithValidDefaults(traderBusinessName = "A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength")
    }

    "reject if trader business name is more than the maximum length" in new WithApplication {
      formWithValidDefaults(traderBusinessName = "A" * BusinessName.MaxLength + 1).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength")
    }

    "accept if trader business name is valid" in new WithApplication {
      formWithValidDefaults(traderBusinessName = TraderBusinessNameValid, traderPostcode = PostcodeValid).
        get.traderBusinessName should equal(TraderBusinessNameValid.toUpperCase)
    }
  }

  "postcode" should {
    "reject if trader postcode is empty" in new WithApplication {
      // IMPORTANT: The messages being returned by the form validation are overridden by the Controller
      val errors = formWithValidDefaults(traderPostcode = "").errors
      errors should have length 3
      errors(0).key should equal(TraderPostcodeId)
      errors(0).message should equal("error.minLength")
      errors(1).key should equal(TraderPostcodeId)
      errors(1).message should equal("error.required")
      errors(2).key should equal(TraderPostcodeId)
      errors(2).message should equal("error.restricted.validPostcode")
    }

    "reject if trader postcode is less than the minimum length" in new WithApplication {
      formWithValidDefaults(traderPostcode = "M15A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validPostcode")
    }

    "reject if trader postcode is more than the maximum length" in new WithApplication {
      formWithValidDefaults(traderPostcode = "SA99 1DDD").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength", "error.restricted.validPostcode")
    }

    "reject if trader postcode contains special characters" in new WithApplication {
      formWithValidDefaults(traderPostcode = "SA99 1D$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }

    "reject if trader postcode contains an incorrect format" in new WithApplication {
      formWithValidDefaults(traderPostcode = "SAR99").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }
  }

  private def formWithValidDefaults(traderBusinessName: String = TraderBusinessNameValid,
                                    traderPostcode: String = PostcodeValid, traderEmail: Option[String] = None) = {

    implicit val cookieFlags = new NoCookieFlags()
    implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()

    new SetUpTraderDetailsTesting()
      .form.bind(
        Map(
          TraderNameId -> traderBusinessName,
          TraderPostcodeId -> traderPostcode
        ) ++ traderEmail.fold(Map(TraderEmailOptionId -> OptionalToggle.Invisible)) { email =>
          Map(
            TraderEmailOptionId -> OptionalToggle.Visible,
            s"$TraderEmailId.$EmailEnterId" -> email,
            s"$TraderEmailId.$EmailVerifyId" -> email
          )
        }
      )
  }
}
