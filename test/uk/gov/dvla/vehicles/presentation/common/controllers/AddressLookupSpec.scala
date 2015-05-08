package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.libs.json.Json
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import uk.gov.dvla.vehicles.presentation.common
import common.model.Address
import common.{WithApplication, UnitSpec}
import common.clientsidesession.{ClientSideSession, ClientSideSessionFactory}
import common.webserviceclients.addresslookup.AddressLookupService
import org.mockito.Mockito.stub
import org.mockito.Matchers.any
//import uk.gov.dvla.vehicles.presentation.common.controllers.AddressLookup.AddressDTO
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, OK, INTERNAL_SERVER_ERROR}

import scala.concurrent.{Future, Await}

class AddressLookupSpec extends UnitSpec {
  val postCode = "E14 9LL"
  val trackingId = "test-tracking-id"
  val lookupService = mock[AddressLookupService]
  val sessionFactory = mock[ClientSideSessionFactory]
  val session = mock[ClientSideSession]
  stub(session.trackingId).toReturn(trackingId)
  stub(sessionFactory.getSession(any[Traversable[Cookie]])).toReturn(session)

  val request = FakeRequest()

  "lookup address by postcode" should {

    "return a list of addresses as 200 json" in {
      test200(Seq(
        (postCode, "a, b, c, London, W3W 5NT", AddressDTO("", "a, b, c", None, None, "London", postCode)),
        (postCode, "x, y, z, Chester, W4W 6NT", AddressDTO("", "x, y, z", None, None, "Chester", postCode)))
      )


      "return a list of addresses as 200 json" in {
        test200(Seq(
          (postCode, "a, b, c, London, W3W 5NT", AddressDTO("", "a, b, c", None, None, "London", postCode)),
          (postCode, "x, y, z, Chester, W4W 6NT", AddressDTO("", "x, y, z", None, None, "Chester", postCode)))
        )
      }
    }

    "return 500 with message if service returns unsuccessfully future" in {
      val exc = new Exception("Some message in the exception")
      stub(lookupService.fetchAddressesForPostcode(postCode, trackingId)).toReturn(
        Future.failed(exc)
      )

      val fr = new AddressLookup(lookupService)(sessionFactory).byPostcode(postCode)(request)

      val response = Await.result(fr, 5 seconds).header
      response.status should equal(INTERNAL_SERVER_ERROR)
      response.headers.get("content-type").get should equal("text/plain; charset=utf-8")
      contentAsString(fr) should equal(exc.getMessage)
    }

    def test200(addresses: Seq[(String, String, AddressDTO)]) = {
      stub(lookupService.fetchAddressesForPostcode(postCode, trackingId)).toReturn(
        Future.successful(addresses.map{case (postCode, addressLine, _)=> (postCode, addressLine)})
      )

      val fr = new AddressLookup(lookupService)(sessionFactory).byPostcode(postCode)(request)
      val response = Await.result(fr, 5 seconds).header
      response.status should equal(OK)
      response.headers.get("content-type").get should equal("application/json; charset=utf-8")

      implicit val JsonFormat = Json.format[Address]
      Json.fromJson[Array[Address]](Json.parse(contentAsString(fr))).asEither match {
        case Left(errors) => fail(errors.mkString(", "))
        case Right(model) => model should equal(addresses.map{case (_, _, address)=> address}.toArray)
      }
    }
  }
}
