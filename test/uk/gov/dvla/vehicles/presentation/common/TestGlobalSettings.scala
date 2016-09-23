package uk.gov.dvla.vehicles.presentation.common

import play.api.GlobalSettings
import play.api.test.{FakeApplication, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.TestWithApplication.fakeAppWithTestGlobal
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.MicroServices

object TestGlobalSettings extends GlobalSettings

abstract class TestWithApplication(testApp: FakeApplication = fakeAppWithTestGlobal) extends WithApplication(testApp)

object TestWithApplication {
  private lazy val fakeAppWithTestGlobal: FakeApplication = LightFakeApplication(TestGlobalSettings, MicroServices.DefaultBaseUrls)
}
