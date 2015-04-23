package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import scala.concurrent.ExecutionContext.Implicits.global

class AddressLookup @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {
  def byPostcode(postCode: String) = Action.async { request =>
    val session = clientSideSessionFactory.getSession(request.cookies)
    addressLookup.fetchAddressesForPostcode(postCode, session.trackingId)
      .map(a => Ok(""))
  }
}
