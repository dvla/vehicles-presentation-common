package uk.gov.dvla.vehicles.presentation.common.controllers

import uk.gov.dvla.vehicles.presentation.common.composition.WithTestApplication
import uk.gov.dvla.vehicles.presentation.common.helpers.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.mappings.BusinessName.MaxLength
import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common.controllers
import models.BusinessNameModel.Form.BusinessNameId

final class BusinessNameFormSpec extends UnitSpec {

  "form" should {
    "accept if form is valid with business name field filled in" in new WithTestApplication {
      val model = formWithValidDefaults().get
      model.name should equal(BusinessNameValid.toUpperCase)
    }
  }

  "dealerName" should {
    "reject if business name is blank" in new WithTestApplication {
      // IMPORTANT: The messages.en being returned by the form validation are overridden by the Controller
      val errors = formWithValidDefaults(businessName = "").errors
      errors should have length 3
      errors(0).key should equal(BusinessNameId)
      errors(0).message should equal("error.minLength")
      errors(1).key should equal(BusinessNameId)
      errors(1).message should equal("error.required")
      errors(2).key should equal(BusinessNameId)
      errors(2).message should equal("error.validBusinessName")
    }

    "reject if business name is less than minimum length" in new WithTestApplication {
      formWithValidDefaults(businessName = "A").errors should have length 1
    }

    "reject if business name is more than the maximum length" in new WithTestApplication {
      formWithValidDefaults(businessName = "A" * MaxLength + 1).errors should have length 1
    }
  }

  private final val BusinessNameValid = "Test name"
  private def formWithValidDefaults(businessName: String = BusinessNameValid) = {

    injector.getInstance(classOf[controllers.BusinessNameController])
      .form.bind(
        Map(BusinessNameId -> businessName)
      )
  }
}
