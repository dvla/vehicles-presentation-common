package composition

import app.ConfigProperties.getProperty
import com.google.inject.name.Names
import com.tzavellas.sse.guice.ScalaModule
import common.{CookieFlags, NoCookieFlags, ClientSideSessionFactory, ClearTextClientSideSessionFactory}
import filters.AccessLoggingFilter.AccessLoggerName
import org.scalatest.mock.MockitoSugar
import play.api.{LoggerLike, Logger}
import serviceclients.fakes.FakeVehicleLookupWebService
import serviceclients.fakes.FakeDisposeWebServiceImpl
import serviceclients.fakes.FakeDateServiceImpl
import serviceclients.fakes.FakeAddressLookupWebServiceImpl
import serviceclients.address_lookup.{AddressLookupWebService, AddressLookupService}
import serviceclients.vehicle_lookup.{VehicleLookupServiceImpl, VehicleLookupService, VehicleLookupWebService}
import serviceclients.dispose_service.{DisposeServiceImpl, DisposeWebService, DisposeService}
import serviceclients.brute_force_prevention.BruteForcePreventionWebService
import serviceclients.brute_force_prevention.BruteForcePreventionService
import serviceclients.brute_force_prevention.BruteForcePreventionServiceImpl
import serviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl
import services.DateService

class TestModule() extends ScalaModule with MockitoSugar {
  /**
   * Bind the fake implementations the traits
   */
  def configure() {
    Logger.debug("Guice is loading TestModule")

    getProperty("addressLookupService.type", "ordnanceSurvey") match {
      case "ordnanceSurvey" => ordnanceSurveyAddressLookup()
      case _ => gdsAddressLookup()
    }
    bind[VehicleLookupWebService].to[FakeVehicleLookupWebService].asEagerSingleton()
    bind[VehicleLookupService].to[VehicleLookupServiceImpl].asEagerSingleton()
    bind[DisposeWebService].to[FakeDisposeWebServiceImpl].asEagerSingleton()
    bind[DisposeService].to[DisposeServiceImpl].asEagerSingleton()
    bind[DateService].to[FakeDateServiceImpl].asEagerSingleton()
    bind[CookieFlags].to[NoCookieFlags].asEagerSingleton()
    bind[ClientSideSessionFactory].to[ClearTextClientSideSessionFactory].asEagerSingleton()

    bind[BruteForcePreventionWebService].to[FakeBruteForcePreventionWebServiceImpl].asEagerSingleton()
    bind[BruteForcePreventionService].to[BruteForcePreventionServiceImpl].asEagerSingleton()
    bind[LoggerLike].annotatedWith(Names.named(AccessLoggerName)).toInstance(Logger("dvla.common.AccessLogger"))
  }

  private def ordnanceSurveyAddressLookup() = {
    bind[AddressLookupService].to[serviceclients.address_lookup.ordnance_survey.AddressLookupServiceImpl]

    val fakeWebServiceImpl = new FakeAddressLookupWebServiceImpl(
      responseOfPostcodeWebService = FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress,
      responseOfUprnWebService = FakeAddressLookupWebServiceImpl.responseValidForUprnToAddress
    )
    bind[AddressLookupWebService].toInstance(fakeWebServiceImpl)
  }

  private def gdsAddressLookup() = {
    bind[AddressLookupService].to[serviceclients.address_lookup.gds.AddressLookupServiceImpl]
    val fakeWebServiceImpl = new FakeAddressLookupWebServiceImpl(
      responseOfPostcodeWebService = FakeAddressLookupWebServiceImpl.responseValidForGdsAddressLookup,
      responseOfUprnWebService = FakeAddressLookupWebServiceImpl.responseValidForGdsAddressLookup
    )
    bind[AddressLookupWebService].toInstance(fakeWebServiceImpl)
  }
}