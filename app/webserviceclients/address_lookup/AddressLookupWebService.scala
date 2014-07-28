package webserviceclients.address_lookup

import scala.concurrent.Future
import play.api.libs.ws.Response
import play.api.i18n.Lang

// Wrapper around our webservice call so that we can IoC fake versions for testing or use the real version.
trait AddressLookupWebService {

  def callPostcodeWebService(postcode: String, trackingId: String)
                            (implicit lang: Lang): Future[Response]

  def callUprnWebService(uprn: String, trackingId: String)
                        (implicit lang: Lang): Future[Response]
}