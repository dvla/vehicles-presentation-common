package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClientSideSession, ClientSideSessionFactory}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import org.mockito.Mockito.stub
import org.mockito.Matchers.{any, anyString}

import scala.concurrent.Future

class AddressLookupSpec extends UnitSpec {
  "lookup address by postcode" should {
    "return a list of addresses as json" in {
      val trackingId = "test-tracking-id"
      val lookupService = mock[AddressLookupService]
      val sessionFactory = mock[ClientSideSessionFactory]
      val session = mock[ClientSideSession]

      stub(session.trackingId).toReturn(trackingId)
      stub(sessionFactory.getSession(any[Traversable[Cookie]])).toReturn(session)
      stub(lookupService.fetchAddressesForPostcode(anyString, trackingId)).toReturn(
        Future.successful(Seq(("", ""), ("", "")))
      )
    }
  }
}
