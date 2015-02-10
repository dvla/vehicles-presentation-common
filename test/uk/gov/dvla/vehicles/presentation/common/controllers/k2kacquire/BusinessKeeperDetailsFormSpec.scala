package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

//import _root_.pages.acquire.BusinessKeeperDetailsPage._
//import composition.WithApplication
//import controllers.BusinessKeeperDetails

import play.api.mvc.{Result, Request}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.{WithApplication, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.k2kandacquire.models.BusinessKeeperDetailsFormModel.Form.{FleetNumberId, BusinessNameId, EmailId, PostcodeId}
import uk.gov.dvla.vehicles.presentation.common.mappings.BusinessKeeperName

class BusinessKeeperDetailsFormSpec extends UnitSpec {

  final val FleetNumberValid = "123456"
  final val BusinessNameValid = "Brand New Motors"
  final val EmailValid = "my@email.com"
  final val PostcodeValid = "QQ99QQ"
  final val PostcodeInvalid = "XX99XX"

  "form" should {
    "accept if form is completed with all fields correct" in new WithApplication {
      val model = formWithValidDefaults().get
      model.fleetNumber should equal(Some(FleetNumberValid))
      model.businessName should equal(BusinessNameValid.toUpperCase)
      model.email should equal(Some(EmailValid))
    }

    "accept if form is completed with mandatory fields only" in new WithApplication {
      val model = formWithValidDefaults(
        fleetNumber = "",
        email = ""
      ).get
      model.fleetNumber should equal(None)
      model.businessName should equal(BusinessNameValid.toUpperCase)
      model.email should equal(None)
    }

    "reject if form has no fields completed" in new WithApplication {
      formWithValidDefaults(fleetNumber = "", businessName = "", email = "").
        errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required", "error.validBusinessKeeperName")
    }
  }

  "businessName" should {
    "reject if business name is blank" in new WithApplication {
      // IMPORTANT: The messages being returned by the form validation are overridden by the Controller
      val errors = formWithValidDefaults(businessName = "").errors
      errors should have length 3
      errors(0).key should equal(BusinessNameId)
      errors(0).message should equal("error.minLength")
      errors(1).key should equal(BusinessNameId)
      errors(1).message should equal("error.required")
      errors(2).key should equal(BusinessNameId)
      errors(2).message should equal("error.validBusinessKeeperName")
    }

    "reject if business keeper name is less than minimum length" in new WithApplication {
      formWithValidDefaults(businessName = "A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength")
    }

    "reject if business keeper name is more than the maximum length" in new WithApplication {
      formWithValidDefaults(businessName = "A" * BusinessKeeperName.MaxLength + 1)
        .errors.flatMap(_.messages) should contain theSameElementsAs List("error.maxLength")
    }
  }

  "postcode" should {
    "reject if postcode is empty" in new WithApplication {
      formWithValidDefaults(postcode = "M15A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validPostcode")
    }

    "reject if postcode is less than the minimum length" in new WithApplication {
      formWithValidDefaults(postcode = "M15A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validPostcode")
    }

    "reject if postcode is more than the maximum length" in new WithApplication {
      formWithValidDefaults(postcode = "SA99 1DDD").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength", "error.restricted.validPostcode")
    }

    "reject if postcode contains special characters" in new WithApplication {
      formWithValidDefaults(postcode = "SA99 1D$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }

    "reject if postcode contains an incorrect format" in new WithApplication {
      formWithValidDefaults(postcode = "SAR99").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }

    "accept when a valid postcode is entered" in new WithApplication {
      val model = formWithValidDefaults(postcode = PostcodeValid).get
      model.postcode should equal(PostcodeValid)
    }
  }



  private def formWithValidDefaults(fleetNumber: String = FleetNumberValid,
                                    businessName: String = BusinessNameValid,
                                    email: String = EmailValid,
                                    postcode: String = PostcodeValid) = {
    implicit val cookieFlags = new NoCookieFlags()
    implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()

    new BusinessKeeperDetailsTesting()
      .form.bind(
        Map(
          FleetNumberId -> fleetNumber,
          BusinessNameId -> businessName,
          EmailId -> email,
          PostcodeId -> postcode
        )
      )
  }
}