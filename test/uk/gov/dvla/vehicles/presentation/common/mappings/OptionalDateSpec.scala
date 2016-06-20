package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.data.{Form, FormError}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeDateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, mappings}

class OptionalDateSpec extends UnitSpec {

  case class OptionalDateModel(date: Option[LocalDate])

    final val OptionalForm = Form(mapping(
      "optional" -> mappings.OptionalDate.optionalDateMapping
    )(OptionalDateModel.apply)(OptionalDateModel.unapply))

    "Optional date mapping" should {
      "Bind with empty data" in {
        OptionalForm.bind(
          Map("optional.day" -> "", "optional.month" -> "", "optional.year" -> "")
        ).value should ===(Some(OptionalDateModel(None)))

        OptionalForm.bind(Map[String, String]()).value should ===(Some(OptionalDateModel(None)))
      }

      "Bind correctly when all the parameters are provided" in {
        OptionalForm.bind(
          Map("optional.day" -> "01", "optional.month" -> "02", "optional.year" -> "1934")
        ).value should ===(Some(OptionalDateModel(Some(new LocalDate(1934, 2, 1)))))
      }

      "Fail to bind when there are some errors in the values provided" in {
        val form = OptionalForm.bind(
          Map("optional.day" -> "&^", "optional.month" -> "1", "optional.year" -> "1951")
        )
        form.value should ===(None)
        form.errors should ===(Seq(FormError("optional", "error.date.invalid")))
      }
    }

    "Optional date of birth mapping" should {
      implicit val dateService = new FakeDateServiceImpl
      val dateOfBirthForm = Form(mapping(
        "dateOfBirth" -> mappings.DateOfBirth.optionalDateOfBirth
      )(OptionalDateModel.apply)(OptionalDateModel.unapply))

      "Allow no date to be entered" in {
        val bound = dateOfBirthForm.bind(
          Map("required.day" -> "", "required.month" -> "", "required.year" -> "")
        )
        bound.value should ===(Some(OptionalDateModel(None)))
        bound.errors should be(empty)
      }
    }

}
