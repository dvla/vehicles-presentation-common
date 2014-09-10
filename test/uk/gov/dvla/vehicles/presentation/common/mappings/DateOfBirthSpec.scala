package uk.gov.dvla.vehicles.presentation.common.mappings

import com.github.nscala_time.time.Imports._
import play.api.data.Forms.mapping
import play.api.data.{Form, FormError}
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, mappings}

class DateOfBirthSpec extends UnitSpec {
  case class OptionalDateOfBirthModel(dateOfBirth: Option[LocalDate])
  case class RequiredDateOfBirthModel(dateOfBirth: LocalDate)

  final val OptionalForm = Form(mapping(
    "optional" -> mappings.DateOfBirth.optionalMapping
  )(OptionalDateOfBirthModel.apply)(OptionalDateOfBirthModel.unapply))

  final val RequiredForm = Form(mapping(
    "required" -> mappings.DateOfBirth.mapping
  )(RequiredDateOfBirthModel.apply)(RequiredDateOfBirthModel.unapply))

  "Required date of birth mapping" should {
    "Bind correctly when all the parameters are provided and are valid" in {
      def validateBind(day: Int, month: Int, year: Int) = RequiredForm.bind(
        Map("required.day" -> s"$day", "required.month" -> s"$month", "required.year" -> s"$year")
      ).value should ===(Some(RequiredDateOfBirthModel(new LocalDate(year, month, day))))

      validateBind(1, 2, 3)
      validateBind(28, 2, 4)
      validateBind(29, 2, 2000)
      validateBind(29, 3, -2000)
    }

    "Fail to bind when there are some errors in the values provided" in {
      val form = RequiredForm.bind(
        Map("required.day" -> "&^", "required.month" -> "1", "required.year" -> "1111")
      )

      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.dateOfBirth.invalid")))
    }

    "Fail to bind with empty data" in {
      val form = RequiredForm.bind(Map("required.day" -> "", "required.month" -> "", "required.year" -> ""))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.dateOfBirth.invalid")))

      val form1 = RequiredForm.bind(Map[String, String]())
      form1.value should ===(None)
      form1.errors should ===(Seq(FormError("required", "error.dateOfBirth.invalid")))
    }

    "Fail to bind with invalid day" in {
      def validateInvalidDay(day: String, month: String = "3", year: String = "1111") =
        validateInvalidDate(day, month, year)
      validateInvalidDay("0")
      validateInvalidDay("29", "2", "2001")
      validateInvalidDay("32", "3")
      validateInvalidDay("31", "4")
      validateInvalidDay("*&^*")
      validateInvalidDay("-1")
      validateInvalidDay("")
    }

    "Fail to bind with invalid month" in {
      def validateInvalidMonth(month: String) = validateInvalidDate("5", month, "1111")
      validateInvalidMonth("0")
      validateInvalidMonth("29")
      validateInvalidMonth("32")
      validateInvalidMonth("31")
      validateInvalidMonth("*&^*")
      validateInvalidMonth("-1")
      validateInvalidMonth("")
      validateInvalidMonth("sdf")
    }

    "Fail to bind with invalid year" in {
      def validateInvalidYear(year: String) = validateInvalidDate("5", "6", year)
      validateInvalidYear("")
      validateInvalidYear("sdfsdf")
      validateInvalidYear("*&^*")
    }

    "Fail to bind to a date in the future" in {
      val tomorrow = LocalDate.tomorrow
      val form = RequiredForm.bind(Map(
        "required.day" -> tomorrow.getDayOfMonth.toString,
        "required.month" -> tomorrow.getMonthOfYear.toString,
        "required.year" -> tomorrow.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.dateOfBirth.inTheFuture")))
    }
  }

  "Optional date of birth mapping" should {
    "bind with empty data" in {
      OptionalForm.bind(
        Map("optional.day" -> "", "optional.month" -> "", "optional.year" -> "")
      ).value should ===(Some(OptionalDateOfBirthModel(None)))

      OptionalForm.bind(Map[String, String]()).value should ===(Some(OptionalDateOfBirthModel(None)))
    }

    "Bind correctly when all the parameters are provided" in {
      OptionalForm.bind(
        Map("optional.day" -> "1", "optional.month" -> "2", "optional.year" -> "1234")
      ).value should ===(Some(OptionalDateOfBirthModel(Some(new LocalDate(1234, 2, 1)))))
    }

    "Fail to bind when there are some errors in the values provided" in {
      val form = OptionalForm.bind(
        Map("optional.day" -> "&^", "optional.month" -> "1", "optional.year" -> "1111")
      )
      form.value should ===(None)
      form.errors should ===(Seq(FormError("optional", "error.dateOfBirth.invalid")))
    }
  }

  private def validateInvalidDate(day: String, month: String, year: String): Unit = {
    val form = RequiredForm.bind(Map("required.day" -> day, "required.month" -> month, "required.year" -> year))
    form.value should ===(None)
    form.errors should ===(Seq(FormError("required", "error.dateOfBirth.invalid")))
  }
}
