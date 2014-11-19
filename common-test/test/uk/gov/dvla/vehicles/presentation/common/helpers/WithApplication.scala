package uk.gov.dvla.vehicles.presentation.common.helpers

import webbrowser.TestGlobal
import WithApplication.fakeAppWithTestGlobal
import play.api.test.FakeApplication

abstract class WithApplication(app: FakeApplication = fakeAppWithTestGlobal)
  extends play.api.test.WithApplication(app = app)

object WithApplication {
  private lazy val fakeAppWithTestGlobal: FakeApplication = FakeApplication(withGlobal = Some(TestGlobal))
}
