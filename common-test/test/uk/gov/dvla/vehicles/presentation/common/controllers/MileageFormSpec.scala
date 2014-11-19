package uk.gov.dvla.vehicles.presentation.common.controllers

import uk.gov.dvla.vehicles.presentation.common.helpers.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.models

import uk.gov.dvla.vehicles.presentation.common.mappings.Mileage.MaxLength
import models.MileageModel.Form.MileageId

final class MileageFormSpec extends UnitSpec {

  "form" should {
    "accept if form is valid with mileage field filled in" in {
      val model = formWithValidDefaults().get
      model.mileage.get should equal(ValidMileage.toInt)
    }

    "accept if form is valid with mileage field not filled in" in {
      val model = formWithValidDefaults(mileage = "").get
      model.mileage should equal(None)
    }
  }

  "mileage" should {
    "reject if entered mileage is negative" in {
      formWithValidDefaults(mileage = "-123").errors should have length 1
    }

    "reject if entered mileage is more than the maximum length" in {
      formWithValidDefaults(mileage = "9" * MaxLength + 1).errors should have length 1
    }

    "reject if entered mileage is not numeric" in {
      formWithValidDefaults(mileage = "Boom").errors should have length 1
    }

    "reject if entered mileage contains decimal" in {
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
