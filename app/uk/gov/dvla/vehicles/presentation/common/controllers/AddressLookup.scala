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

class AddressLookup @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  def byPostcode(postCode: String) = Action.async { request =>
    val session = clientSideSessionFactory.getSession(request.cookies)
    //    implicit val writes = Json.format[AddressDTO]
    addressLookup.addresses(postCode, session.trackingId).map { addressLines =>
      Ok(Json.toJson(addressLines))
    } recover {
      case NonFatal(e) => InternalServerError(e.getMessage)
    }
  }
}
