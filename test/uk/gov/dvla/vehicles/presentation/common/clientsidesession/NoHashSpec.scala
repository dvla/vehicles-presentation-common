package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, TestWithApplication}

class NoHashSpec extends UnitSpec {

  "NoHash" should {
    "return a clear text string" in new TestWithApplication {
      noHash.hash(ClearText) should equal(ClearText)
    }

    "return expected length for the digest" in new TestWithApplication {
      noHash.digestStringLength should equal(0)
    }
  }

  private val noHash = new NoHashGenerator
  // Sharing immutable fixture objects via instance variables
  private final val ClearText = "qwerty"
}