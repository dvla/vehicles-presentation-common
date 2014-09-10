package uk.gov.dvla.vehicles.presentation.common.mappings

import uk.gov.dvla.vehicles.presentation.common.UnitSpec

final class MileageUnitSpec extends UnitSpec {

  /**
   * Test valid mileage formats
   */
  val validMileage = Seq("1", "123456")
  validMileage.map(mileage => s"indicate the mileage is valid: $mileage" in {
    val result = isValidMileage(mileage)
    result should equal(true)
  })

  /**
   * Test invalid mileage formats
   */
  val invalidMileage = Seq("", "1234567", "-12")
  invalidMileage.map(mileage => s"indicate the mileage is not valid: $mileage" in {
    val result = isValidMileage(mileage)
    result should equal(false)
  })

  private def isValidMileage(mileage: String): Boolean = {
    val regex = Mileage.Pattern.r
    if (regex.pattern.matcher(mileage).matches) true
    else false
  }
}
