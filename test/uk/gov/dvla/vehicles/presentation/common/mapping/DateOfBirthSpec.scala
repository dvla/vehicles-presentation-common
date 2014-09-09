package uk.gov.dvla.vehicles.presentation.common.mapping

import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.dvla.vehicles.presentation.common.views.models.DateOfBirth
import uk.gov.dvla.vehicles.presentation.common.{mappings, UnitSpec}

class DateOfBirthSpec extends UnitSpec {
  case class OptionalDateOfBirthModel(dateOfBirth: Option[DateOfBirth])
  case class RequiredDateOfBirthModel(dateOfBirth: DateOfBirth)

  final val OptionalForm = Form(mapping(
    "optional" -> mappings.DateOfBirth.optionalDateOfBirth
  )(OptionalDateOfBirthModel.apply)(OptionalDateOfBirthModel.unapply))

  final val RequiredForm = Form(mapping(
    "required" -> mappings.DateOfBirth.requiredDateOfBirth
  )(RequiredDateOfBirthModel.apply)(RequiredDateOfBirthModel.unapply))

  "Required date of birth mapping" should {
    "Bind correctly when all the parameters are provided" in {
      RequiredForm.bind(
        Map("required.day" -> "1", "required.month" -> "1", "required.year" -> "1111")
      ).value should not be None
    }

    "Fail to bind when there are some errors in the values provided" in {
      RequiredForm.bind(
        Map("required.day" -> "&^", "required.month" -> "1", "required.year" -> "1111")
      ).value should be(None)
    }

    "Fail to bind with empty data" in {
      RequiredForm.bind(
        Map("required.day" -> "", "required.month" -> "", "required.year" -> "")
      ).value should be(None)
    }
  }

  "Optional date of birth mapping" should {
    "bind with empty data" in {
      OptionalForm.bind(
        Map("optional.day" -> "", "optional.month" -> "", "optional.year" -> "")
      ).value.get.dateOfBirth should be(None)
    }

    "Bind correctly when all the parameters are provided" in {
      OptionalForm.bind(
        Map("optional.day" -> "1", "optional.month" -> "1", "optional.year" -> "1111")
      ).value.get should not be None
    }

    "Fail to bind when there are some errors in the values provided" in {
      OptionalForm.bind(
        Map("optional.day" -> "&^", "optional.month" -> "1", "optional.year" -> "1111")
      ).value should be(None)
    }
  }
}
