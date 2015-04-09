package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Around
import org.specs2.specification.Scope
import play.api.test.{FakeApplication, Helpers}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.TestConfiguration.configureTestUrl
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication

abstract class WithDefaultApplication extends Around with Scope with GlobalCreator {

  private implicit def implicitApp: FakeApplication = LightFakeApplication(global)

  override def around[T: AsResult](t: => T): Result = configureTestUrl() {
    Helpers.running(implicitApp)(AsResult.effectively(t))
  }
}
