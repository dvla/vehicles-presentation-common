package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode
import uk.gov.dvla.vehicles.presentation.common.model.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddressLookup @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {
  def byPostcode(postCode: String) = Action.async { request =>
//    val session = clientSideSessionFactory.getSession(request.cookies)
    if (postCode == "123") Future.successful{InternalServerError("Some error message")}
    else {
      implicit val writes = Json.format[Address]
      Future.successful(Ok(Json.toJson(Seq(
        Address("a1", Some("a2"), Some("a3"), "a4", postCode, true),
        Address("b1", Some("b2"), Some("b3"), "b4", postCode, true),
        Address("c1", Some("c2"), Some("c3"), "c4", postCode, false),
        Address("d1", Some("d2"), Some("d3"), "d4", postCode, false)
      ))))
    }
//    addressLookup.fetchAddressesForPostcode(postCode, session.trackingId)
//      .map(a => Ok(""))
  }
}
