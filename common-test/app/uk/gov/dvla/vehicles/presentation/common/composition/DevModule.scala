package uk.gov.dvla.vehicles.presentation.common.composition

import com.tzavellas.sse.guice.ScalaModule
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{NoCookieFlags, CookieFlags, ClearTextClientSideSessionFactory, ClientSideSessionFactory}
import common.services.{DateServiceImpl, DateService}
import common.utils.helpers.{CommonConfig, ConfigImpl}
import common.webserviceclients.addresslookup.{AddressLookupWebService, AddressLookupService}
import common.webserviceclients.addresslookup.ordnanceservey.{WebServiceImpl, AddressLookupServiceImpl}
import common.webserviceclients.healthstats.HealthStats

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
    bind[CommonConfig].to[ConfigImpl].asEagerSingleton()
    bind[ClientSideSessionFactory].to[ClearTextClientSideSessionFactory].asEagerSingleton()
    bind[CookieFlags].to[NoCookieFlags].asEagerSingleton()
    bind[DateService].to[DateServiceImpl].asEagerSingleton()
    bind[HealthStats].asEagerSingleton()
    bind[AddressLookupService].to[AddressLookupServiceImpl].asEagerSingleton()
    bind[AddressLookupWebService].to[WebServiceImpl].asEagerSingleton()
  }
}
