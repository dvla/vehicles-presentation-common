package uk.gov.dvla.vehicles.presentation.common

class LogFormatsUnitSpec extends UnitSpec {

  "Anonymize" should {
    "empty string should return null" in {
      val inputString: String = null
      LogFormats.anonymize(inputString) should equal("null")
    }

    "string of greater than 8 characters should return 4 characters and the rest stars" in {
      val inputString = "qwertyuiop"
      LogFormats.anonymize(inputString) should equal("******uiop")
    }

    "string of less than 8 characters should return half characters and the rest stars" in {
      val inputString = "qwer"
      LogFormats.anonymize(inputString) should equal("**er")
    }

    "string of 8 characters should return half characters and the rest stars" in {
      val inputString = "qwertyui"
      LogFormats.anonymize(inputString) should equal("****tyui")
    }

    "string with an odd number of characters should return more than half stars and the remainder characters" in {
      val inputString = "qwert"
      LogFormats.anonymize(inputString) should equal("***rt")
    }

    "string with 1 character should replace it with a star" in {
      val inputString = "q"
      LogFormats.anonymize(inputString) should equal("*")
    }

    "string with 4 characters should return 2 characters and the rest stars" in {
      val inputString = "qwer"
      LogFormats.anonymize(inputString) should equal("**er")
    }
  }
}