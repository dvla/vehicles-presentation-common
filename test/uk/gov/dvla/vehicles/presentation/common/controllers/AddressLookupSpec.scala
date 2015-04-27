package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.libs.json.{JsString, Json}
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import uk.gov.dvla.vehicles.presentation.common
import common.model.Address
import common.{WithApplication, UnitSpec}
import common.clientsidesession.{ClientSideSession, ClientSideSessionFactory}
import common.webserviceclients.addresslookup.AddressLookupService
import org.mockito.Mockito.stub
import org.mockito.Matchers.{any, anyString, eq => equalMatch}
import org.scalatest.concurrent.Futures
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, LOCATION, OK, status}

import scala.concurrent.Await

class AddressLookupSpec extends UnitSpec {
  "lookup address by postcode" should {
    "return a list of addresses as json" in new WithApplication {
      val postCode = "E14 9LL"
      val trackingId = "test-tracking-id"
      val lookupService = mock[AddressLookupService]
      val sessionFactory = mock[ClientSideSessionFactory]
      val session = mock[ClientSideSession]

      stub(session.trackingId).toReturn(trackingId)
      stub(sessionFactory.getSession(any[Traversable[Cookie]])).toReturn(session)

      val request = FakeRequest()
//      stub(lookupService.fetchAddressesForPostcode(anyString, equalMatch(trackingId))).toReturn(
//        Future.successful(Seq((postCode, "a, b, c, d, e", "f"), (postCode, "x, y, z, r, s, t")))
//      )

//      var fr = new AddressLookup(lookupService)(sessionFactory).byPostcode(postCode)(request)
//
//      Await.result(fr, 5 seconds).header.status should equal(OK)
//      Json.format[Array[Address]].reads(JsString(contentAsString(fr))).asOpt.get should equal(
//        Array(Address("a", Some("b"), Some("c"), "d", Some("e"), "f")),
//        Array(Address("x", Some("y"), Some("z"), "r", Some("s"), "t"))
//      )
    }
  }
}
