package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies


class AddressLookup @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  def byPostcode(postCode: String) = Action.async { request =>
    val session = clientSideSessionFactory.getSession(request.cookies)
    addressLookup.addresses(postCode, session.trackingId).map { addressLines =>
      Ok(Json.toJson(addressLines))
    } recover {
      case NonFatal(e) =>
        Logger.warn(s"${e.getMessage} ${e.getStackTraceString} - trackingId: ${request.cookies.trackingId()}")
        InternalServerError(e.getMessage)
    }
  }
}
