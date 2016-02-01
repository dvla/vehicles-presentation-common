package uk.gov.dvla.vehicles.presentation.common.controllers

// Used to test the version controller. Used in the routes file.
class VersionControllerTest extends Version(VersionControllerTest.testUrl)

object VersionControllerTest {
  lazy val testUrl = uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.TestConfiguration.testUrl
}
