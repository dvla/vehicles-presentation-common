package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class ByteGeneratorSpec extends UnitSpec {

  "ByteGenerator" should {
    "return a correctly sized array of bytes when requested" in {
      val randomBytes: Array[Byte] = ByteGenerator.getSecureRandomBytes(16)
      randomBytes.length should equal(16)
    }

    "return a different array of bytes each time it is called" in {
      val randomBytes1: Array[Byte] = ByteGenerator.getSecureRandomBytes(4)
      val randomBytes2: Array[Byte] = ByteGenerator.getSecureRandomBytes(4)
      randomBytes1.sameElements(randomBytes2) should equal(false)
    }
  }
}