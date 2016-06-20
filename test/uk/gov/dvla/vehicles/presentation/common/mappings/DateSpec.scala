package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.{Form, FormError}
import play.api.data.Forms.mapping
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakePastDateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeFutureDateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeDateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, mappings}
import DateSpec.RequiredDateModel

class DateSpec extends UnitSpec {
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
        validateInvalidRequiredDate(day, month, year)
      validateInvalidDay("0")
      validateInvalidDay("29", "2", "2001")
      validateInvalidDay("32", "3")
      validateInvalidDay("31", "4")
      validateInvalidDay("*&^*")
      validateInvalidDay("-1")
      validateInvalidDay("")
    }

    "Fail to bind with invalid month" in {
      def validateInvalidMonth(month: String) = validateInvalidRequiredDate("5", month, "1951")
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
      def validateInvalidYear(year: String) = validateInvalidRequiredDate("5", "6", year)
      validateInvalidYear("")
      validateInvalidYear("sdfsdf")
      validateInvalidYear("*&^*")
    }

    "Fail to bind with less then 4 characters" in {
      validateInvalidRequiredDate("5", "6", "1")
      validateInvalidRequiredDate("5", "6", "11")
      validateInvalidRequiredDate("5", "6", "111")
    }
  }

  "Not before and not after constraints" should {

    "Disallow future dates, and allow the same dates as they become past dates" in {
      val today = LocalDate.now()

      val notAfterFormPast = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture()(new FakePastDateServiceImpl))
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val formNotAfterFormPast = notAfterFormPast.bind(Map(
        "required.day" -> today.toString("dd"),
        "required.month" -> today.toString("MM"),
        "required.year" -> today.toString("YYYY")
      ))
      formNotAfterFormPast.value should === (None)
      formNotAfterFormPast.errors should === (Seq(FormError("required", "error.date.inTheFuture")))

      val notAfterFormFuture = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture()(new FakeFutureDateServiceImpl))
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val formNotAfterFormFuture = notAfterFormFuture.bind(Map(
        "required.day" -> today.toString("dd"),
        "required.month" -> today.toString("MM"),
        "required.year" -> today.toString("YYYY")
      ))
      formNotAfterFormFuture.value should ===(Some(RequiredDateModel(new LocalDate(today.getYear, today.getMonthOfYear, today.getDayOfMonth))))
      formNotAfterFormFuture.errors should ===(Seq.empty)
    }

    "Invalidate an date in the future" in {
      implicit val dateService = new FakeDateServiceImpl
      val notInTheFutureForm = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture())
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val tomorrow = LocalDate.now().plusDays(1)
      val form = notInTheFutureForm.bind(Map(
        "required.day" -> tomorrow.toString("dd"),
        "required.month" -> tomorrow.toString("MM"),
        "required.year" -> tomorrow.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.inTheFuture")))
    }

    "Invalidate an year before some date" in {
      val yesturday = LocalDate.now().minusDays(1)
      val notBeforeForm = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notBefore(yesturday))
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val yesturdayMinusOne = yesturday.minusDays(1)
      val form = notBeforeForm.bind(Map(
        "required.day" -> yesturdayMinusOne.toString("dd"),
        "required.month" -> yesturdayMinusOne.toString("MM"),
        "required.year" -> yesturdayMinusOne.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.notBefore")))
    }

    "Invalidate an year in the past" in {
      implicit val dateService = new FakeDateServiceImpl
      val notInThePastForm = Form(mapping(
        "required" -> mappings.Date.dateMapping.verifying(mappings.Date.notInThePast())
      )(RequiredDateModel.apply)(RequiredDateModel.unapply))

      val yesturday = LocalDate.now().minusDays(1)
      val form = notInThePastForm.bind(Map(
        "required.day" -> yesturday.toString("dd"),
        "required.month" -> yesturday.toString("MM"),
        "required.year" -> yesturday.getYear.toString
      ))
      form.value should ===(None)
      form.errors should ===(Seq(FormError("required", "error.date.notInThePast")))
    }
  }

  "Unbind should populate the fields of the map" in {
    val formData = RequiredDateModel(new LocalDate(1961, 2, 3))
    RequiredForm.bind(RequiredForm.fill(formData).data).value should ===(Some(formData))
  }

  private def validateInvalidRequiredDate(day: String, month: String, year: String): Unit =
    DateSpec.validateInvalidRequiredDate(RequiredForm, day, month, year)

}

object DateSpec extends UnitSpec {
  case class RequiredDateModel(date: LocalDate)

  def validateInvalidRequiredDate(form: Form[_],
                                  day: String,
                                  month: String,
                                  year: String,
                                  message: String = "error.date.invalid"): Unit = {
    val form1 = form.bind(Map("required.day" -> day, "required.month" -> month, "required.year" -> year))
    form1.value should ===(None)
    form1.errors should ===(Seq(FormError("required", message)))
  }

  def validateValidRequiredDate(form: Form[_], day: String, month: String, year: String) = form.bind(
    Map("required.day" -> s"$day", "required.month" -> s"$month", "required.year" -> s"$year")
  ).value should ===(Some(RequiredDateModel(new LocalDate(year.toInt, month.toInt, day.toInt))))
}
