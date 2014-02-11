package controllers.disposal_of_vehicle

import org.scalatest.{Matchers, WordSpec}

class DisposeFormSpec extends WordSpec with Matchers {
  "Dispose Form" should {

    val consentValid = "true"
    val mileageValid = "20000"
    val dateOfDisposalDayValid = "25"
    val dateOfDisposalMonthValid = "11"
    val dateOfDisposalYearValid = "1970"

    def disposeFormFiller(consent: String, mileage: String, day: String, month: String, year: String) = {
      Dispose.disposeForm.bind(
        Map(
          "consent" -> consent,
          "mileage" -> mileage,
          "dateOfDisposal.day" -> day,
          "dateOfDisposal.month" -> month,
          "dateOfDisposal.year" -> year
        )
      )
    }

    "reject if consent is not ticked" in {
      disposeFormFiller(consent = "false", mileage = mileageValid, day = dateOfDisposalDayValid, month = dateOfDisposalMonthValid, year = dateOfDisposalYearValid).fold (
        formWithErrors => {
          formWithErrors.errors.length should equal(1)
        },
        f => fail("An error should occur")
      )
    }

    "reject if date day is invalid" in {
      disposeFormFiller(consent = consentValid, mileage = mileageValid, day = "", month = dateOfDisposalMonthValid, year = dateOfDisposalYearValid).fold (
        formWithErrors => {
          formWithErrors.errors.length should equal(1)
        },
        f => fail("An error should occur")
      )
    }

    "reject if date month is invalid" in {
      disposeFormFiller(consent = consentValid, mileage = mileageValid, day = dateOfDisposalDayValid, month = "", year = dateOfDisposalYearValid).fold (
        formWithErrors => {
          formWithErrors.errors.length should equal(1)
        },
        f => fail("An error should occur")
      )
    }

    "reject if date year is invalid" in {
      disposeFormFiller(consent = consentValid, mileage = mileageValid, day = dateOfDisposalDayValid, month = dateOfDisposalMonthValid, year = "").fold (
        formWithErrors => {
          formWithErrors.errors.length should equal(1)
        },
        f => fail("An error should occur")
      )
    }

  }
}