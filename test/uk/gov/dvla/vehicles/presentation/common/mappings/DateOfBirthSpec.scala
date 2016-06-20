package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, mappings}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeDateServiceImpl
import DateSpec.{RequiredDateModel, validateInvalidRequiredDate, validateValidRequiredDate}

class DateOfBirthSpec extends UnitSpec {

  "Date of birth mapping" should {
    implicit val dateService = new FakeDateServiceImpl
    val dateOfBirthForm = Form(mapping(
      "required" -> mappings.DateOfBirth.dateOfBirth()
    )(RequiredDateModel.apply)(RequiredDateModel.unapply))

    "Validate is not in the future" in {
      val tomorrow = LocalDate.now().plusDays(1)

      validateInvalidRequiredDate(
        dateOfBirthForm,
        tomorrow.toString("dd"),
        tomorrow.toString("MM"),
        tomorrow.getYear.toString,
        "error.dateOfBirth.inTheFuture"
      )

      val today = LocalDate.now()
      validateValidRequiredDate(
        dateOfBirthForm,
        today.toString("dd"),
        today.toString("MM"),
        today.toString("YYYY")
      )
    }

    "Date is not optional" in {
      validateInvalidRequiredDate(dateOfBirthForm, "", "", "", "error.dateOfBirth.invalid")
      validateInvalidRequiredDate(dateOfBirthForm, "1", "1", "", "error.dateOfBirth.invalid")
      validateInvalidRequiredDate(dateOfBirthForm, "1", "", "1", "error.dateOfBirth.invalid")
      validateInvalidRequiredDate(dateOfBirthForm, "", "1", "1", "error.dateOfBirth.invalid")
    }

    "Date is no more then 110 years in the past" in {
      validateInvalidRequiredDate(
        dateOfBirthForm,
        "01",
        "01",
        LocalDate.now().minusYears(111).getYear.toString,
        "error.dateOfBirth.110yearsInThePast"
      )
    }
  }

}
