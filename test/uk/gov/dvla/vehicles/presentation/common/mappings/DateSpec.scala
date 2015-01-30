package uk.gov.dvla.vehicles.presentation.common.mappings

import com.github.nscala_time.time.Imports._
import play.api.data.Forms.mapping
import play.api.data.{Form, FormError}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakePastDateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeFutureDateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeDateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, mappings}

class DateSpec extends UnitSpec {
  case class OptionalDateModel(date: Option[LocalDate])
  case class RequiredDateModel(date: LocalDate)

  final val OptionalForm = Form(mapping(
    "optional" -> mappings.Date.optionalDateMapping
  )(OptionalDateModel.apply)(OptionalDateModel.unapply))

  final val RequiredForm = Form(mapping(
    "required" -> mappings.Date.dateMapping
  )(RequiredDateModel.apply)(RequiredDateModel.unapply))

  "Required date mapping" should {
    "Bind correctly when all the parameters are provided and are valid" in {
      def validateBind(day: String, month: String, year: Int) = RequiredForm.bind(
        Map("required.day" -> s"$day", "required.month" -> s"$month", "required.year" -> s"$year")
      ).value should ===(Some(RequiredDateModel(new LocalDate(year, month.toInt, day.toInt))))

      validateBind("01", "02", 1000)
      validateBind("01", "02", 3419)
      validateBind("01", "02", 1943)
      validateBind("28", "02", 1944)
      validateBind("29", "02", 2000)
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
    }

    "Fail to bind with less then 4 characters" in {
      validateInvalidDate("5", "6", "1")
      validateInvalidDate("5", "6", "11")
      validateInvalidDate("5", "6", "111")
    }
  }

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

  "Not before and not after constraints" should {

    "Disallow future dates, and allow the same dates as they become past dates" in {
      val notAfterFormPast = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture()(new FakePastDateServiceImpl))
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val formNotAfterFormPast = notAfterFormPast.bind(Map(
        "required.day" -> "01",
        "required.month" -> "01",
        "required.year" -> "2015"
      ))
      formNotAfterFormPast.value should === (None)
      formNotAfterFormPast.errors should === (Seq(FormError("required", "error.date.inTheFuture")))

      val notAfterFormFuture = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture()(new FakeFutureDateServiceImpl))
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val formNotAfterFormFuture = notAfterFormFuture.bind(Map(
        "required.day" -> "01",
        "required.month" -> "01",
        "required.year" -> "2015"
      ))
      formNotAfterFormFuture.value should ===(Some(RequiredDateModel(new LocalDate(2015, 1, 1))))
      formNotAfterFormFuture.errors should ===(Seq.empty)
    }

    "Invalidate an year after some date" in {
      val notAfterForm = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notAfter(LocalDate.tomorrow))
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val tomorrowPlusOne = LocalDate.tomorrow.plusDays(1)
      val form = notAfterForm.bind(Map(
        "required.day" -> tomorrowPlusOne.toString("dd"),
        "required.month" -> tomorrowPlusOne.toString("MM"),
        "required.year" -> tomorrowPlusOne.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.notAfter")))
    }

    "Invalidate an date in the future" in {
      implicit val dateService = new FakeDateServiceImpl
      val notInTheFutureForm = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture())
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val tomorrowPlusOne = LocalDate.tomorrow
      val form = notInTheFutureForm.bind(Map(
        "required.day" -> tomorrowPlusOne.toString("dd"),
        "required.month" -> tomorrowPlusOne.toString("MM"),
        "required.year" -> tomorrowPlusOne.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.inTheFuture")))
    }

    "Invalidate an year before some date" in {
      val notBeforeForm = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notBefore(LocalDate.yesterday))
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val tomorrowPlusOne = LocalDate.yesterday.minusDays(1)
      val form = notBeforeForm.bind(Map(
        "required.day" -> tomorrowPlusOne.toString("dd"),
        "required.month" -> tomorrowPlusOne.toString("MM"),
        "required.year" -> tomorrowPlusOne.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.notBefore")))
    }

    "Invalidate an year in the past" in {
      implicit val dateService = new FakeDateServiceImpl
      val notInThePastForm = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInThePast())
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val tomorrowPlusOne = LocalDate.yesterday
      val form = notInThePastForm.bind(Map(
        "required.day" -> tomorrowPlusOne.toString("dd"),
        "required.month" -> tomorrowPlusOne.toString("MM"),
        "required.year" -> tomorrowPlusOne.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.notInThePast")))
    }
  }

  "Date of birth mapping" should {
    implicit val dateService = new FakeDateServiceImpl
    val dateOfBirthForm = Form(mapping(
      "required" -> mappings.Date.dateOfBirth()
    )(RequiredDateModel.apply)(RequiredDateModel.unapply))

    "Validate is not in the future" in {
      val tomorrow = LocalDate.tomorrow
      validateInvalidDate(
        dateOfBirthForm,
        tomorrow.toString("dd"),
        tomorrow.toString("MM"),
        tomorrow.getYear.toString,
        "error.dateOfBirth.inTheFuture"
      )

      val today = LocalDate.today
      validateValidDate(
        dateOfBirthForm,
        today.toString("dd"),
        today.toString("MM"),
        today.toString("YYYY")
      )
    }

    "Date is not optional" in {
      validateInvalidDate(dateOfBirthForm, "", "", "", "error.dateOfBirth.invalid")
      validateInvalidDate(dateOfBirthForm, "1", "1", "", "error.dateOfBirth.invalid")
      validateInvalidDate(dateOfBirthForm, "1", "", "1", "error.dateOfBirth.invalid")
      validateInvalidDate(dateOfBirthForm, "", "1", "1", "error.dateOfBirth.invalid")
    }

    "Date is no more then 110 years in the past" in {
      validateInvalidDate(
        dateOfBirthForm,
        "01",
        "01",
        LocalDate.today.minusYears(111).getYear.toString,
        "error.dateOfBirth.110yearsInThePast"
      )
    }
  }

  "Optional date of birth mapping" should {
    implicit val dateService = new FakeDateServiceImpl
    val dateOfBirthForm = Form(mapping(
      "dateOfBirth" -> mappings.Date.optionalDateOfBirth
    )(OptionalDateModel.apply)(OptionalDateModel.unapply))

    "Allow no date to be entered" in {
      val bound = dateOfBirthForm.bind(
        Map("required.day" -> "", "required.month" -> "", "required.year" -> "")
      )
      bound.value should ===(Some(OptionalDateModel(None)))
      bound.errors should be(empty)
    }
  }

  "Unbind should populate the fields of the map" in {
    val formData = RequiredDateModel(new LocalDate(1961, 2, 3))
    RequiredForm.bind(RequiredForm.fill(formData).data).value should ===(Some(formData))
  }

  private def validateInvalidDate(day: String, month: String, year: String): Unit =
    validateInvalidDate(RequiredForm, day, month, year)

  private def validateInvalidDate(form: Form[_],
                                  day: String,
                                  month: String,
                                  year: String,
                                  message: String = "error.date.invalid"): Unit = {
    val form1 = form.bind(Map("required.day" -> day, "required.month" -> month, "required.year" -> year))
    form1.value should ===(None)
    form1.errors should ===(Seq(FormError("required", message)))
  }

  private def validateValidDate(form: Form[_], day: String, month: String, year: String) = form.bind(
    Map("required.day" -> s"$day", "required.month" -> s"$month", "required.year" -> s"$year")
  ).value should ===(Some(RequiredDateModel(new LocalDate(year.toInt, month.toInt, day.toInt))))
}
