package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import akka.util.Timeout
import play.api.data.Form
import play.api.i18n.Lang
import play.api.test.FakeRequest
import play.api.test.Helpers.contentAsString
import uk.gov.dvla.vehicles.presentation.common
import common.testhelpers.CookieFactoryForUnitSpecs.{privateKeeperDetailsCookie, vehicleAndKeeperDetailsCookie}
import common.testhelpers.CookieFactoryForUnitSpecs.businessKeeperDetailsCookie
import common.testhelpers.CookieFactoryForUnitSpecs.{PostcodeValid, defaultKeeperChooseYourAddressViewModel}

import common.{WithApplication, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieFactoryForUnitSpecs
import scala.concurrent.duration.DurationInt
import uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire.PrivateKeeperDetailsTesting.presentTestResult
import uk.gov.dvla.vehicles.presentation.common.model.{AddressModel, CacheKeyPrefix}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService

import scala.concurrent.{Await, Future, ExecutionContext}
import ExecutionContext.Implicits.global
import scala.language.postfixOps

final class NewKeeperChooseYourAddressUnitSpec extends UnitSpec {

  implicit val timeoutTimer = Timeout(1 second)
  implicit val cookieFlags = new NoCookieFlags()
  implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()
  implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")
  implicit val AddressService: AddressLookupService = new AddressLookupService {
    override def fetchAddressesForPostcode(postcode: String,
                                           trackingId: String,
                                           showBusinessName: Option[Boolean])
                                          (implicit lang: Lang): Future[Seq[(String, String)]] =
      Future(Seq((PostcodeValid, "not an address")))

    override def fetchAddressForUprn(uprn: String, trackingId: String)(implicit lang: Lang) =
      Future(Some(AddressModel(None, Seq("not an address"))))
  }

  private def controller = new NewKeeperChooseYourAddressBaseTesting(AddressService)

  def verifyInvalidForm(form: Form[_]): Unit =
    form.fold(
      invalidForm => "Just return a string - this is the success case",
      validModel => throw new Exception("Error extracting model from form")
    )

  "present (use UPRN enabled)" should {
    "display the page if private new keeper details cached" in new WithApplication {
      val newKeeperChooseYourAddress = controller
      val request = FakeRequest().withCookies(privateKeeperDetailsCookie(), vehicleAndKeeperDetailsCookie())

      val result = Await.result(newKeeperChooseYourAddress.present(request), 5 seconds)
      result.header.status should equal(presentTestResult.header.status)

      newKeeperChooseYourAddress.presentResultArgs.head.vehicleDetails should equal(
        defaultKeeperChooseYourAddressViewModel.vehicleDetails
      )
    }
  }

  "display selected field when private new keeper cookie exists" in new WithApplication {
    val newKeeperChooseYourAddress = controller
    val request = FakeRequest().withCookies(privateKeeperDetailsCookie(), 
      vehicleAndKeeperDetailsCookie(),
      CookieFactoryForUnitSpecs.newKeeperChooseYourAddress(12345L.toString))

    val result = newKeeperChooseYourAddress.present(request)
    val content = contentAsString(result)
    content should equal("presentResult")
  }

  "display selected field when business new keeper cookie exists" in new WithApplication {
    val newKeeperChooseYourAddress = controller
    val request = FakeRequest().withCookies(businessKeeperDetailsCookie(),
      vehicleAndKeeperDetailsCookie(),
      CookieFactoryForUnitSpecs.newKeeperChooseYourAddress(12345L.toString))

    val result = newKeeperChooseYourAddress.present(request)
    val content = contentAsString(result)
    content should equal("presentResult")
  }

  "redirect to vehicle lookup page when present is called with no keeper details cached" in new WithApplication {
    val newKeeperChooseYourAddress = controller
    val request = FakeRequest().withCookies(vehicleAndKeeperDetailsCookie())

    val result = newKeeperChooseYourAddress.present(request)
    val content = contentAsString(result)
    content should equal("vehicleLookupTestResult")
  }
}