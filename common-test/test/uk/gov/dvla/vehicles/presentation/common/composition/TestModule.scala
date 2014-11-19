package uk.gov.dvla.vehicles.presentation.common.composition

import com.tzavellas.sse.guice.ScalaModule
import org.scalatest.mock.MockitoSugar
import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{ClearTextClientSideSessionFactory, ClientSideSessionFactory, CookieFlags, NoCookieFlags}

class TestModule() extends ScalaModule with MockitoSugar {
  /**
   * Bind the test implementations to the traits
   */
  def configure() {
    Logger.debug("Guice is loading TestModule")

    bind[CookieFlags].to[NoCookieFlags].asEagerSingleton()
    bind[ClientSideSessionFactory].to[ClearTextClientSideSessionFactory].asEagerSingleton()
  }
}
