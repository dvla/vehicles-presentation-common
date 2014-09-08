package controllers

import uk.gov.dvla.vehicles.presentation.common.mappings.BusinessName.MaxLength
import models.BusinessNameModel.Form.BusinessNameId
import helpers.UnitSpec

final class BusinessNameFormSpec extends UnitSpec {

  "form" should {
    "accept if form is valid with business name field filled in" in {
      val model = formWithValidDefaults().get
      model.name should equal(BusinessNameValid)
    }
  }

  "dealerName" should {
    "reject if business name is blank" in {
      // IMPORTANT: The messages being returned by the form validation are overridden by the Controller
      val errors = formWithValidDefaults(businessName = "").errors
      errors should have length 3
      errors(0).key should equal(BusinessNameId)
      errors(0).message should equal("error.minLength")
      errors(1).key should equal(BusinessNameId)
      errors(1).message should equal("error.required")
      errors(2).key should equal(BusinessNameId)
      errors(2).message should equal("error.validBusinessName")
    }

    "reject if business name is less than minimum length" in {
      formWithValidDefaults(businessName = "A").errors should have length 2
    }

    "reject if business name is more than the maximum length" in {
      formWithValidDefaults(businessName = "A" * MaxLength + 1).errors should have length 1
    }
  }

  private final val BusinessNameValid = "Test name"
  private def formWithValidDefaults(businessName: String = BusinessNameValid) = {

    injector.getInstance(classOf[BusinessNameController])
      .form.bind(
        Map(BusinessNameId -> businessName)
      )
  }
}
