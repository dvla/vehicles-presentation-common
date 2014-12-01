package uk.gov.dvla.vehicles.presentation.common.controllers

// Used to test the version controller. Used in the routes file.
class VersionControllerTest extends Version(VersionControllerTest.missingUrl, VersionControllerTest.notParsingUrl)

object VersionControllerTest {
  val notParsingUrl = "not parsing url"
  val missingUrl = "missing url"
}
