package uk.gov.dvla.vehicles.presentation.common.controllers

import uk.gov.dvla.vehicles.presentation.common.helpers.TestWithApplication
import uk.gov.dvla.vehicles.presentation.common.helpers.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.mappings.Mileage.MaxLength
import uk.gov.dvla.vehicles.presentation.common.models.MileageModel.Form.MileageId

class MileageFormSpec extends UnitSpec {

  "form" should {
    "accept if form is valid with mileage field filled in" in new TestWithApplication {
      val model = formWithValidDefaults().get
      model.mileage.get should equal(ValidMileage.toInt)
    }

    "accept if form is valid with mileage field not filled in" in new TestWithApplication {
      val model = formWithValidDefaults(mileage = "").get
      model.mileage should equal(None)
    }
  }

  "mileage" should {
    "reject if entered mileage is negative" in new TestWithApplication {
      formWithValidDefaults(mileage = "-123").errors should have length 1
    }

    "reject if entered mileage is more than the maximum length" in new TestWithApplication {
      formWithValidDefaults(mileage = "9" * MaxLength + 1).errors should have length 1
    }

    "reject if entered mileage is not numeric" in new TestWithApplication {
      formWithValidDefaults(mileage = "Boom").errors should have length 1
    }

    "reject if entered mileage contains decimal" in new TestWithApplication {
      formWithValidDefaults(mileage = "12.45").errors should have length 1
    }
  }

  private final val ValidMileage = "1234"
  private def formWithValidDefaults(mileage: String = ValidMileage) = {
    injector.getInstance(classOf[MileageController])
      .form.bind(
        Map(MileageId -> mileage)
      )
  }
}
