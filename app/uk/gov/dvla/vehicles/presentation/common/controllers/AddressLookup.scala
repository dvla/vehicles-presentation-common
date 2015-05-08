package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Result, Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

case class AddressDTO(addressLine: String,
                      streetAddress1: String,
                      streetAddress2: Option[String],
                      streetAddress3: Option[String],
                      postTown: String,
                      postCode: String)

class AddressLookup @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  def byPostcode(postCode: String) = Action.async { request =>
    val session = clientSideSessionFactory.getSession(request.cookies)
    implicit val writes = Json.format[AddressDTO]
    addressLookup.fetchAddressesForPostcode(postCode, session.trackingId).map { addressLines =>
      val addresses = addressLines.map { case (postCode, encoded) =>
        val addressElements = encoded.split(",").map(_.trim)
        val addressLines = addressElements.dropRight(2)
        AddressDTO(encoded, addressLines.mkString(", "), None, None, addressElements.takeRight(2).head, postCode)
      }
      Ok(Json.toJson(addresses))
    } recover {
      case NonFatal(e) => InternalServerError(e.getMessage)
    }
  }
}

