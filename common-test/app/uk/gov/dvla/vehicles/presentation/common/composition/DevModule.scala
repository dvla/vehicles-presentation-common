package uk.gov.dvla.vehicles.presentation.common.composition

import com.tzavellas.sse.guice.ScalaModule
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{NoCookieFlags, CookieFlags, ClearTextClientSideSessionFactory, ClientSideSessionFactory}

/**
 * Provides implementations of traits
 * Note the use of sse-guice, which is a library that makes the Guice internal DSL more scala friendly
 * eg we can write this:
 * bind[Service].to[ServiceImpl].in[Singleton]
 * instead of this:
 * bind(classOf[Service]).to(classOf[ServiceImpl]).in(classOf[Singleton])
 *
 * Look in build.scala for where we import the sse-guice library
 */
class DevModule extends ScalaModule {
  def configure() {
    bind[ClientSideSessionFactory].to[ClearTextClientSideSessionFactory].asEagerSingleton()
    bind[CookieFlags].to[NoCookieFlags].asEagerSingleton()
  }
}
