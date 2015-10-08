package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup

import play.api.i18n.Lang
import play.api.libs.ws.WSResponse
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.{AddressResponseDto, AddressDto}

trait AddressLookupService {

  def fetchAddressesForPostcode(postcode: String, trackingId: TrackingId)
                               (implicit lang: Lang): Future[Seq[(String, String)]]

  def addresses(postcode: String, trackingId: TrackingId)
               (implicit lang: Lang): Future[Seq[AddressDto]]

  def toDropDownFormat(addresses: Seq[AddressResponseDto]): Seq[(String, String)]

}
