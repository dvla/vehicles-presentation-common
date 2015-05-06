package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

class AddressLookup @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {
  def byPostcode(postCode: String) = Action.async { request =>
    val session = clientSideSessionFactory.getSession(request.cookies)
    implicit val writes = Json.format[Address]
    addressLookup.fetchAddressesForPostcode(postCode, session.trackingId).map { addressLines =>
      val addresses = addressLines.map { case (postCode, encoded) =>
        val addressElements = encoded.split(",").map(_.trim)
        val addressLines = addressElements.dropRight(2)
        Address(addressLines.mkString(", "), None, None, addressElements.takeRight(2).head, postCode, false)
      }
      Ok(Json.toJson(addresses))
    } recover {
      case NonFatal(e) => InternalServerError(e.getMessage)
    }
  }
}
