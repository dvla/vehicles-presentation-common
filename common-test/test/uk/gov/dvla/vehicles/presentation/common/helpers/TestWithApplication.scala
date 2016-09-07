package uk.gov.dvla.vehicles.presentation.common.helpers

import uk.gov.dvla.vehicles.presentation.common.composition.TestGlobalWithFilters
import uk.gov.dvla.vehicles.presentation.common.helpers.TestWithApplication.fakeAppWithTestGlobal
import play.api.test.{FakeApplication, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.MicroServices

abstract class TestWithApplication(testApp: FakeApplication = fakeAppWithTestGlobal) extends WithApplication(testApp)

object TestWithApplication {
  private lazy val fakeAppWithTestGlobal: FakeApplication = LightFakeApplication(TestGlobalWithFilters, MicroServices.DefaultBaseUrls)
}