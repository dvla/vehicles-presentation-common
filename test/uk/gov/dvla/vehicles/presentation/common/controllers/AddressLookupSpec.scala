package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.libs.json.Json
import play.api.mvc.{Request, Cookie}
import play.api.test.FakeRequest
import uk.gov.dvla.vehicles.presentation.common
import common.UnitSpec
import common.clientsidesession.{ClientSideSession, ClientSideSessionFactory}
import common.webserviceclients.addresslookup.AddressLookupService
import org.mockito.Mockito.stub
import org.mockito.Matchers.any
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressDto

import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import play.api.test.Helpers._

import scala.concurrent.{Future, Await}

class AddressLookupSpec extends UnitSpec {
  val postCode = "E14 9LL"
  val trackingId = "test-tracking-id"
  implicit val lookupService = mock[AddressLookupService]
  implicit val sessionFactory = mock[ClientSideSessionFactory]
  val session = mock[ClientSideSession]
  stub(session.trackingId).toReturn(trackingId)
  stub(sessionFactory.getSession(any[Traversable[Cookie]])).toReturn(session)

  val request = FakeRequest()

  "lookup address by postcode" should {

    "return a list of addresses as 200 json" in {
      test200(Seq(
        AddressDto(s"a, b, c, London, $postCode", None, "a, b, c", None, None, "London", postCode),
        AddressDto(s"", Some("business"), "x, y, z, Chester, $postCode", None, None, "Chester", postCode))
      )
    }

    "return 500 with message if service returns unsuccessfully future" in {
      val exc = new Exception("Some message in the exception")
      stub(lookupService.addresses(postCode, trackingId)).toReturn(
        Future.failed(exc)
      )

      val fr = new AddressLookup(){override def authenticate(r: Request[_]) = true}.byPostcode(postCode)(request)

      val response = Await.result(fr, 5 seconds).header
      response.status should equal(INTERNAL_SERVER_ERROR)
      response.headers.get("content-type").get should equal("text/plain; charset=utf-8")
      contentAsString(fr) should equal(exc.getMessage)
    }

    "Fail if no authentication cookie is specified" in {
      val fr = new AddressLookup{override def authenticate(r: Request[_]) = false}.byPostcode(postCode)(request)
      val response = Await.result(fr, 5 seconds).header
      response.status should equal(UNAUTHORIZED)
      response.headers.get("content-type").get should equal("text/plain; charset=utf-8")
    }

    def test200(addresses: Seq[AddressDto]) = {
      stub(lookupService.addresses(postCode, trackingId)).toReturn(
        Future.successful(addresses)
      )

      val fr = new AddressLookup{override def authenticate(r: Request[_]) = true}.byPostcode(postCode)(request)
      val response = Await.result(fr, 5 seconds).header
      response.status should equal(OK)
      response.headers.get("content-type").get should equal("application/json; charset=utf-8")

      implicit val JsonFormat = Json.format[AddressDto]
      Json.fromJson[Array[AddressDto]](Json.parse(contentAsString(fr))).asEither match {
        case Left(errors) => fail(errors.mkString(", "))
        case Right(model) => model should equal(addresses.toArray)
      }
    }
  }
}
