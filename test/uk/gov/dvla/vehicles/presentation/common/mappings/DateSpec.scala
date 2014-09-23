package uk.gov.dvla.vehicles.presentation.common.mappings

import com.github.nscala_time.time.Imports._
import play.api.data.Forms.mapping
import play.api.data.{Form, FormError}
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, mappings}
import com.github.nscala_time.time.RichLocalDate

class DateSpec extends UnitSpec {
  case class OptionalDateModel(date: Option[LocalDate])
  case class RequiredDateModel(date: LocalDate)

  final val OptionalForm = Form(mapping(
    "optional" -> mappings.Date.optionalNonFutureDateMapping
  )(OptionalDateModel.apply)(OptionalDateModel.unapply))

  final val RequiredForm = Form(mapping(
    "required" -> mappings.Date.nonFutureDateMapping
  )(RequiredDateModel.apply)(RequiredDateModel.unapply))

  "Required date of birth mapping" should {
    "Bind correctly when all the parameters are provided and are valid" in {
      def validateBind(day: Int, month: Int, year: Int) = RequiredForm.bind(
        Map("required.day" -> s"$day", "required.month" -> s"$month", "required.year" -> s"$year")
      ).value should ===(Some(RequiredDateModel(new LocalDate(year, month, day))))

      validateBind(1, 2, 1943)
      validateBind(28, 2, 1944)
      validateBind(29, 2, 2000)
      validateBind(29, 3, LocalDate.today.minusYears(110).getYear)
    }

    "Fail to bind when there are some errors in the values provided" in {
      val form = RequiredForm.bind(
        Map("required.day" -> "&^", "required.month" -> "1", "required.year" -> "1951")
      )

      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.invalid")))
    }

    "Fail to bind with empty data" in {
      val form = RequiredForm.bind(Map("required.day" -> "", "required.month" -> "", "required.year" -> ""))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.invalid")))

      val form1 = RequiredForm.bind(Map[String, String]())
      form1.value should ===(None)
      form1.errors should ===(Seq(FormError("required", "error.date.invalid")))
    }

    "Fail to bind with invalid day" in {
      def validateInvalidDay(day: String, month: String = "3", year: String = "1951") =
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
      def validateInvalidMonth(month: String) = validateInvalidDate("5", month, "1951")
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
      validateInvalidYear(LocalDate.today.minusYears(111).getYear.toString)
    }

    "Fail to bind with less then 4 characters" in {
      validateInvalidDate("5", "6", "1")
      validateInvalidDate("5", "6", "11")
      validateInvalidDate("5", "6", "111")
    }

    "Fail to bind to a date in the future" in {
      val tomorrow = LocalDate.tomorrow
      val form = RequiredForm.bind(Map(
        "required.day" -> tomorrow.getDayOfMonth.toString,
        "required.month" -> tomorrow.getMonthOfYear.toString,
        "required.year" -> tomorrow.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.inTheFuture")))
    }
  }

  "Optional date of birth mapping" should {
    "Bind with empty data" in {
      OptionalForm.bind(
        Map("optional.day" -> "", "optional.month" -> "", "optional.year" -> "")
      ).value should ===(Some(OptionalDateModel(None)))

      OptionalForm.bind(Map[String, String]()).value should ===(Some(OptionalDateModel(None)))
    }

    "Bind correctly when all the parameters are provided" in {
      OptionalForm.bind(
        Map("optional.day" -> "1", "optional.month" -> "2", "optional.year" -> "1934")
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

  "Unbind should populate the fields of the map" in {
    val formData = RequiredDateModel(new LocalDate(1961, 2, 3))
    RequiredForm.bind(RequiredForm.fill(formData).data).value should ===(Some(formData))
  }

  private def validateInvalidDate(day: String, month: String, year: String): Unit = {
    val form = RequiredForm.bind(Map("required.day" -> day, "required.month" -> month, "required.year" -> year))
    form.value should ===(None)
    form.errors should ===(Seq(FormError("required", "error.date.invalid")))
  }
}
