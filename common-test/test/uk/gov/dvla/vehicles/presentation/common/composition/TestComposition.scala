package uk.gov.dvla.vehicles.presentation.common.composition

import com.google.inject.util.Modules
import com.google.inject.{Guice, Injector, Module}
import com.tzavellas.sse.guice.ScalaModule
import org.scalatest.mock.MockitoSugar
import play.api.Logger

trait TestComposition extends Composition {
  override lazy val injector: Injector = Guice.createInjector(testMod)

  def testModule(module: Module*) = Modules.`override`(testMod).`with`(module: _*)
  def testInjector(module: Module*) = Guice.createInjector(testModule(module: _*))
  private def testMod = Modules.`override`(new DevModule()).`with`(new TestModule)
}

private class TestModule() extends ScalaModule with MockitoSugar {
  /**
   * Bind the test implementations to the traits
   */
  def configure() {
    Logger.debug("Guice is loading TestModule")
  }
}
