package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup

import play.api.i18n.Lang
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressDto

import scala.concurrent.Future

trait AddressLookupService {

  def addresses(postcode: String, trackingId: TrackingId)
               (implicit lang: Lang): Future[Seq[AddressDto]]

  def addressesToDropDown(postcode: String, trackingId: TrackingId)
             (implicit lang: Lang): Future[Seq[(String, String)]]
}
