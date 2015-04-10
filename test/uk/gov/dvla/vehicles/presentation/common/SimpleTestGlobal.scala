package uk.gov.dvla.vehicles.presentation.common

import play.api.GlobalSettings
import play.api.test.FakeApplication
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication

object SimpleTestGlobal extends GlobalSettings

abstract class WithApplication(app: FakeApplication = WithApplication.fakeAppWithTestGlobal)
  extends play.api.test.WithApplication(app = app)

object WithApplication {
  private lazy val fakeAppWithTestGlobal: FakeApplication = LightFakeApplication(SimpleTestGlobal)
}
