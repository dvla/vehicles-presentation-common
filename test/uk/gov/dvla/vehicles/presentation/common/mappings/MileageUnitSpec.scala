package uk.gov.dvla.vehicles.presentation.common.mappings

import uk.gov.dvla.vehicles.presentation.common.UnitSpec

final class MileageUnitSpec extends UnitSpec {

  "mileage" should {

    val validMileage = Seq("1", "123456")

    validMileage.foreach(mileage =>
      s"indicate the mileage is valid: $mileage" in {
        val result = isValidMileage(mileage)
        result should equal(true)
    })

    val invalidMileage = Seq("", "1234567", "-12", "Aaaaaa")

    invalidMileage.foreach(mileage =>
      s"indicate the mileage is not valid: $mileage" in {
        val result = isValidMileage(mileage)
        result should equal(false)
    })
  }

  private def isValidMileage(mileage: String): Boolean = {
    val regex = Mileage.Pattern.r
    if (regex.pattern.matcher(mileage).matches) true
    else false
  }
}
