package composition

import app.ConfigProperties.getProperty
import com.google.inject.name.Names
import com.tzavellas.sse.guice.ScalaModule
import common.ClearTextClientSideSessionFactory
import common.ClientSideSessionFactory
import common.CookieFlags
import common.CookieFlagsFromConfig
import common.EncryptedClientSideSessionFactory
import filters.AccessLoggingFilter.AccessLoggerName
import play.api.{LoggerLike, Logger}
import serviceclients.address_lookup.{AddressLookupWebService, AddressLookupService, ordnance_survey, gds}
import serviceclients.brute_force_prevention.BruteForcePreventionService
import serviceclients.brute_force_prevention.BruteForcePreventionServiceImpl
import serviceclients.brute_force_prevention.BruteForcePreventionWebService
import serviceclients.dispose_service.{DisposeService, DisposeServiceImpl, DisposeWebService, DisposeWebServiceImpl}
import serviceclients.vehicle_lookup.VehicleLookupService
import serviceclients.vehicle_lookup.VehicleLookupServiceImpl
import serviceclients.vehicle_lookup.VehicleLookupWebService
import serviceclients.vehicle_lookup.VehicleLookupWebServiceImpl
import serviceclients.brute_force_prevention
import services.{DateServiceImpl, DateService}
import utils.helpers.{CookieEncryption, AesEncryption, CookieNameHashGenerator, Sha1HashGenerator}

/**
 * Provides real implementations of traits
 * Note the use of sse-guice, which is a library that makes the Guice internal DSL more scala friendly
 * eg we can write this:
 * bind[Service].to[ServiceImpl].in[Singleton]
 * instead of this:
 * bind(classOf[Service]).to(classOf[ServiceImpl]).in(classOf[Singleton])
 *
 * Look in build.scala for where we import the sse-guice library
 */
object DevModule extends ScalaModule {
  def configure() {
    getProperty("addressLookupService.type", "ordnanceSurvey") match {
      case "ordnanceSurvey" =>
        bind[AddressLookupService].to[ordnance_survey.AddressLookupServiceImpl].asEagerSingleton()
        bind[AddressLookupWebService].to[ordnance_survey.WebServiceImpl].asEagerSingleton()
      case _ =>
        bind[AddressLookupService].to[gds.AddressLookupServiceImpl].asEagerSingleton()
        bind[AddressLookupWebService].to[gds.WebServiceImpl].asEagerSingleton()
    }
    bind[VehicleLookupWebService].to[VehicleLookupWebServiceImpl].asEagerSingleton()
    bind[VehicleLookupService].to[VehicleLookupServiceImpl].asEagerSingleton()
    bind[DisposeWebService].to[DisposeWebServiceImpl].asEagerSingleton()
    bind[DisposeService].to[DisposeServiceImpl].asEagerSingleton()
    bind[DateService].to[DateServiceImpl].asEagerSingleton()
    bind[CookieFlags].to[CookieFlagsFromConfig].asEagerSingleton()

    if (getProperty("encryptCookies", default = true)) {
      bind[CookieEncryption].toInstance(new AesEncryption with CookieEncryption)
      bind[CookieNameHashGenerator].toInstance(new Sha1HashGenerator with CookieNameHashGenerator)
      bind[ClientSideSessionFactory].to[EncryptedClientSideSessionFactory].asEagerSingleton()
    } else
      bind[ClientSideSessionFactory].to[ClearTextClientSideSessionFactory].asEagerSingleton()

    bind[BruteForcePreventionWebService].to[brute_force_prevention.WebServiceImpl].asEagerSingleton()
    bind[BruteForcePreventionService].to[BruteForcePreventionServiceImpl].asEagerSingleton()
    bind[LoggerLike].annotatedWith(Names.named(AccessLoggerName)).toInstance(Logger("dvla.common.AccessLogger"))
  }
}